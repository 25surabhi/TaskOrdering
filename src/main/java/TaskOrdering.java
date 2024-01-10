import java.io.InputStream;
import java.util.*;

public class TaskOrdering {

    // Function to perform topological sorting of tasks bfs
    public static List<String> topologicalSort(Map<String, Set<String>> graph) {
        // keep track of the in-degrees of each task in the graph
        Map<String, Integer> indegree = new HashMap<>();

        for (String node : graph.keySet()) {
            indegree.put(node, 0);
        }

        for (Set<String> dependencies : graph.values()) {
            for (String dependency : dependencies) {
                indegree.put(dependency, indegree.get(dependency) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (String node : indegree.keySet()) {
            if (indegree.get(node) == 0) {
                queue.add(node);
            }
        }

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            for (String neighbor : graph.getOrDefault(current, Collections.emptySet())) {
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if (indegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }

    // read input and process task groups
    public static void main(String[] args) {
        // graph of tasks and their dependencies
        Map<String, Set<String>> tasks = new HashMap<>();
        List<String> currentGroup = new ArrayList<>();

        InputStream inputStream = TaskOrdering.class.getResourceAsStream("/input.txt");

        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Ignore '#' and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    if (!currentGroup.isEmpty()) {
                        processTaskGroup(tasks, currentGroup);
                        currentGroup.clear();
                    }
                } else {
                    currentGroup.add(line);
                }
            }

            if (!currentGroup.isEmpty()) {
                processTaskGroup(tasks, currentGroup);
            }
        } catch (Exception e) {
            // Handle invalid input
            System.err.println("Error processing input: " + e.getMessage());
            System.exit(1);
        }
    }

    // process each task group
    private static void processTaskGroup(Map<String, Set<String>> tasks, List<String> currentGroup) {
        for (String line : currentGroup) {
            try {
                String[] parts = line.split(":");
                String task = parts[0].trim();
                String[] dependencies = parts.length > 1 ? parts[1].split(",") : new String[]{};
                tasks.put(task, new HashSet<>(Arrays.asList(dependencies)));
            } catch (Exception e) {
                // Handle exceptions, e.g., invalid task definition
                System.err.println("Error processing task: " + e.getMessage());
                System.exit(1);
            }
        }

        Map<String, Set<String>> graph = new HashMap<>(tasks);
        List<String> order = topologicalSort(graph);

        if (!order.isEmpty()) {
            System.out.println(String.join(" ", order));
        }
    }
}
