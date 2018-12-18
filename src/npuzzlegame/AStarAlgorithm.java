package npuzzlegame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class AStarAlgorithm {

    public static void main(String args[]) {
        
        Scanner sc = new Scanner(System.in);
        
        
        
        // một node sẽ có tối đa 4 trạng thái
        Node[] states = new Node[4];
        Node goalNodeFound = new Node();
        goalNodeFound = null;
        Stack stack = new Stack();
        Stack<ArrayList<Integer>> tableStates = new Stack<>();
        Node current = new Node();
        // Close list
        LinkedList<ArrayList<?>> visited = new LinkedList<>();
        int count = 0;

        //tạo node khởi đầu
        Node start = new Node();
        ArrayList<Integer> startState = new ArrayList<>();
        
        /*
        3 2 0 6 1 5 7 4 8
         */
        try {
            System.out.println("Start game - enter puzzle: ");
            String inputString = sc.nextLine();
            String [] arr = inputString.split(" ");
            for (String string : arr) {
                int block = Integer.parseInt(string);
                startState.add(block);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        System.out.println("Enter your choosing of heuristic: ");
        int flag = sc.nextInt();
        

        start.state = startState;
        start.parent = null;
        start.move = null;
        start.priority = 0;
        start.distance = -1;

        //tạo node đích
        Node goal = new Node();
        ArrayList<Integer> goalState = new ArrayList<>();
        /* 
        0 1 2 
        3 4 5 
        6 7 8
         */

        for (int i = 1; i < 9; i++) {
            goalState.add(i);
        }
        
        goalState.add(0);

        //khởi tạo node đích
        goal.state = goalState;
        goal.parent = null;
        goal.distance = -1;
        goal.move = null;
        
        final long startTime = System.nanoTime();
        // Thuật toán A* 
        Comparator<Node> comparator = new NodeCompare();
        // Sử dụng hàng đợi ưu tiên với 1 bộ so sánh riêng giữa các node
        // Hàng đợi này chính là list Open trong thuật toán
        PriorityQueue<Node> pQ = new PriorityQueue<Node>(100, comparator);
        // đưa node khởi đầu vào list Open
        pQ.add(start);
        // đánh dấu node này đã được thăm
        visited.add(start.state);
        // nếu hàng đợi không rỗng
        while (!pQ.isEmpty()) {
            
            count++;
            // lấy ra phần tử đầu tiên và remove khỏi hàng đợi
            current = pQ.remove();
//            System.out.println(current.priority);
            // tìm trạng thái tiếp theo của node hiện tại
            states = findStates(current);
            // một node sẽ có 4 trạng thái: vì mỗi lần di chuyển ô số chỉ được một lần Lên hoặc Xuống hoặc Trái hoặc Phải
            for (int i = 0; i <= 3; i++) {
                if (states[i] != null) {
                    // nếu trạng một trong 4 trạng thái là trạng thái đích
                    if (states[i].state.equals(goal.state)) {
                        goalNodeFound = states[i];
                        break;
                    } else {
                        // nếu trong list close không chứa trạng thái states[i]
                        if (!visited.contains(states[i].state)) {
                            // trạng thái i sẽ có khoảng cách bằng khoảng cách của node hiện tại đang xét + 1
                            states[i].distance = current.distance + 1;
                            // đưa trạng thái i vào trong list close
                            visited.add(states[i].state);
                            // tính toán chi phí 
                            // mục đích để sắp xếp chi phí ít nhất sẽ được xuất hiện ở đầu hàng đợi -> đây là mục đích của thuật toán A*: lấy ra trạng thái tốn ít chi phí nhất
                            if(flag == 1)
                                states[i].priority = hammingHeuristic(states[i], goal);
                            else 
                                states[i].priority = manhattanHeuristic(states[i], goal);
                            // đưa vào list Open
//                            System.out.println(states[i].priority);
                            pQ.add(states[i]);
                            System.out.println(pQ.peek().priority);
                        }
                    }
                }
            }
            //nếu tìm được đích thì thoát khỏi vòng lặp while
            if (goalNodeFound != null) {
                break;
            }

        }
        
        
        //truy vết hành động của ô trống
        while (goalNodeFound.parent != null) {
            if (goalNodeFound.move != null) {
                stack.push(goalNodeFound.move);
            }
            tableStates.push(goalNodeFound.state);
            goalNodeFound = goalNodeFound.parent;
        }
        
        tableStates.push(startState);
        //số bước đi
        int step = stack.size();
        //in các bước đi của ô trống 
        while (true) {
            if(stack.isEmpty() && tableStates.isEmpty())
                break;
            if(!tableStates.isEmpty())
                
                printStates(tableStates.pop());
            if(!stack.isEmpty())
                System.out.println(stack.pop());
                System.out.print("\033[H\033[2J");
        }

        System.out.println(count + " Nodes expanded.");
        System.out.println("Number of steps: " + step);
        final long duration = System.nanoTime() - startTime;
        System.out.println(duration / 1000000000.0 + " s");
    }

    private static int hammingHeuristic(Node node, Node goal) {
        // TODO Auto-generated method stub

        int priority;
        int count = 0;

        //Hàm Heuristic 
        //h = tổng số ô sai vị trí trong một trạng thái
        for (int i = 0; i < 9; i++) {
            if (!Objects.equals(node.state.get(i), goal.state.get(i))) {
                count++;
            }
        }
        
        // f = g + h
        priority = node.distance + count;
        return priority;
    }

    private static int manhattanHeuristic(Node node, Node goal) {
        // TODO Auto-generated method stub

        int priority;
        int count = 0;
        int index;
        //hàm Heuristic
        //h = tổng khoảng cách Manhattan giữa vị trí hiện tại và vị trí đích của các ô vuông
        for (int i = 0; i < 9; i++) {
            index = goal.state.indexOf(node.state.get(i));
            count = count + Math.abs(index - i);
        }

        priority = node.distance + 2 * count;
        return priority;
    }

    private static Node[] findStates(Node state) {
        // TODO Auto-generated method stub
        // mỗi một node có thể có tối đa 4 trạng thái  
        Node state1, state2, state3, state4;
        //nếu không di chuyển được lên một phía cụ thể thì trạng thái đó sẽ là null
        state1 = moveUP(state);
        state2 = moveDOWN(state);
        state3 = moveLEFT(state);
        state4 = moveRIGHT(state);

        Node[] states = {state1, state2, state3, state4};

        return states;
    }

    private static Node moveRIGHT(Node node) {
        // TODO Auto-generated method stub
        //vị trí của ô trống
        int space = node.state.indexOf(0);
        ArrayList<Integer> childState;
        int temp;
        Node childNode = new Node();
        //ô trống có thể di chuyển sang phải được là ô trống ở các vị trí khác vị trí 2, 5, 8
        if (space != 2 && space != 5 && space != 8) {
            //tạo ra một bản sao của trạng thái state của node đang xét
            childState = (ArrayList<Integer>) node.state.clone();
            //trong childState, lấy phần tử ở bên phải của ô trống
            temp = childState.get(space + 1);
            //sau đó đổi vị trí của ô trống với phần tử bên phải của ô trống
            childState.set(space + 1, 0);
            childState.set(space, temp);
            //bây giờ, node con của node đang xét sẽ có trạng thái mới sau khi đã di chuyển ô trống sang phải
            childNode.state = childState;
            //gán cha của node con là node đang xét
            childNode.parent = node;
            //khoảng cách giữa node con và node cha tăng lên 1
            childNode.distance = node.distance + 1;
            childNode.move = "RIGHT";
            return childNode;
        } else {
            return null;
        }
    }

    // các hàm phía sau đây tương tự như hàm moveRIGHT
    private static Node moveLEFT(Node node) {
        // TODO Auto-generated method stub
        int space = node.state.indexOf(0);
        ArrayList<Integer> childState;
        int temp;
        Node childNode = new Node();

        if (space != 0 && space != 3 && space != 6) {
            childState = (ArrayList<Integer>) node.state.clone();
            temp = childState.get(space - 1);
            childState.set(space - 1, 0);
            childState.set(space, temp);
            childNode.state = childState;
            childNode.parent = node;
            childNode.distance = node.distance + 1;
            childNode.move = "LEFT";
            return childNode;
        } else {
            return null;
        }
    }

    private static Node moveDOWN(Node node) {
        // TODO Auto-generated method stub
        int space = node.state.indexOf(0);
        ArrayList<Integer> childState;
        int temp;
        Node childNode = new Node();

        if (space <= 5) {
            childState = (ArrayList<Integer>) node.state.clone();
            temp = childState.get(space + 3);
            childState.set(space + 3, 0);
            childState.set(space, temp);
            childNode.state = childState;
            childNode.parent = node;
            childNode.distance = node.distance + 1;
            childNode.move = "DOWN";
            return childNode;
        } else {
            return null;
        }
    }

    private static Node moveUP(Node node) {
        // TODO Auto-generated method stub
        int space = node.state.indexOf(0);
        ArrayList<Integer> childState;
        int temp;
        Node childNode = new Node();

        if (space > 2) {
            childState = (ArrayList<Integer>) node.state.clone();
            temp = childState.get(space - 3);
            childState.set(space - 3, 0);
            childState.set(space, temp);
            childNode.state = childState;
            childNode.parent = node;
            childNode.distance = node.distance + 1;
            childNode.move = "UP";
            return childNode;
        } else {
            return null;
        }
    }

    private static void printStates(ArrayList<Integer> state) {
        for (int i = 0; i < state.size(); i++) {
            
            System.out.print(state.get(i) + " ");
            if (((i + 1) % 3 == 0)) {
                System.out.print("\n");
            }
        }
        System.out.println();
    }

}
