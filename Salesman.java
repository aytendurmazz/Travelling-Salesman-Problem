package salesman;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Stack;

class CustomComparator implements Comparator<individual> {

    @Override
    public int compare(individual o1, individual o2) {
        Integer obj1 = o1.getFitness();
        Integer obj2 = o2.getFitness();
        return obj1.compareTo(obj2);
    }
}

class individual {

    private String gnome;
    private int fitness;

    @Override
    public String toString() {
        return "individual{" + "gnome=" + gnome + ", fitness=" + fitness + '}';
    }

    public individual() {
    }

    public String getGnome() {
        return gnome;
    }

    public void setGnome(String gnome) {
        this.gnome = gnome;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
};

public class Salesman {

// Number of cities in TSP
    public static int V = 5;
    public static int POP_SIZE = 10;

    public static void main(String[] args) {
        int[][] map = {
            {0, 2451, 713, 1018, 1631},
            {2451, 0, 1745, 1524, 831},
            {713, 1745, 0, 355, 920},
            {1018, 1524, 355, 0, 700},
            {1631, 831, 920, 700, 0},
            };
        TSPUtil(map);
    }

// Function to return a random number
// from start and end
    public static int rand_num(int start, int end) {
        Random rnd = new Random();
        int r = end - start;
        int rnum = start + rnd.nextInt(r);
        return rnum;
    }

// Function to check if the character
// has already occurred in the string
    public static boolean repeat(String s, char ch) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

// Function to return a mutated GNOME
// Mutated GNOME is a string
// with a random interchange
// of two genes to create variation in species
    public static String mutatedGene(String gnome) {
        char[] ch = gnome.toCharArray();
        while (true) {
            int r = rand_num(1, V);
            int r1 = rand_num(1, V);
            if (r1 != r) {
                char temp = ch[r];
                ch[r] = ch[r1];
                ch[r1] = temp;
                break;
            }
        }
        gnome = String.valueOf(ch);
        return gnome;
    }

// Function to return a valid GNOME string
// required to create the population
    public static String create_gnome() {
        String gnome = "0";
        while (true) {
            if (gnome.length() == V) {
                gnome += gnome.charAt(0);
                break;
            }
            Random rnd = new Random();
            int temp = rnd.nextInt(V - 1) + 1;
            if (!repeat(gnome, (char) (temp + 48))) {
                gnome += (char) (temp + 48);
            }
        }
        return gnome;
    }

// Function to return the fitness value of a gnome.
// The fitness value is the path length
// of the path represented by the GNOME.
    public static int cal_fitness(String gnome) {
        int[][] map =  {
            {0, 2451, 713, 1018, 1631},
            {2451, 0, 1745, 1524, 831},
            {713, 1745, 0, 355, 920},
            {1018, 1524, 355, 0, 700},
            {1631, 831, 920, 700, 0},
            };
        int f = 0;
        for (int i = 0; i < gnome.length() - 1; i++) {
            f += map[Character.getNumericValue(gnome.charAt(i))][Character.getNumericValue(gnome.charAt(i + 1))];
        }
        return f;
    }

// Function to return the updated value
// of the cooling element.
    public static int cooldown(int temp) {
        return (90 * temp) / 100;
    }

// Utility function for TSP problem.
    public static void TSPUtil(int[][] map) {
        // Generation Number
        int gen = 1;
        // Number of Gene Iterations
        int gen_thres = 5;

        //ArrayList<individual> population = new ArrayList<>();
        Stack<individual> population = new Stack<individual>();

        // Populating the GNOME pool.
        for (int i = 0; i < POP_SIZE; i++) {
            individual temp = new individual();
            temp.setGnome(create_gnome());
            temp.setFitness(cal_fitness(temp.getGnome()));
            if (temp.getFitness() != Integer.MAX_VALUE) {
                population.push(temp);
            }

        }
        boolean found = false;
        int temperature = 10000;

        // Iteration to perform
        // population crossing and gene mutation.
        while (temperature > 1000 && gen <= gen_thres) {
            long start = System.nanoTime();
            //population.sort(Comparator.comparing((individual) -> individual.getFitness()));
            Collections.sort(population, new CustomComparator());
            //System.out.println("\nCurrent temp: " + temperature);
            //ArrayList<individual> new_population = new ArrayList<>();
            Stack<individual> new_population = new Stack<individual>();
            for (int i = 0; i < population.size(); i++) {
                individual p1 = population.get(i);

                while (true) {
                    String new_g = mutatedGene(p1.getGnome());
                    individual new_gnome = new individual();
                    new_gnome.setGnome(new_g);
                    new_gnome.setFitness(cal_fitness(new_gnome.getGnome()));

                    if (new_gnome.getFitness() <= population.get(i).getFitness()) {
                        new_population.push(new_gnome);
                        break;
                    } else {

                        // Accepting the rejected children at
                        // a possible probablity above threshold.
                        float prob = (float) Math.pow(2.7, -1 * ((float) (new_gnome.getFitness() - population.get(i).getFitness()) / temperature));
                        if (prob > 0.5) {
                            new_population.push(new_gnome);
                            break;
                        }
                    }
                }
            }

            temperature = cooldown(temperature);
            population = new_population;
            System.out.println("Solution " + gen);
            System.out.println("                GNOME   	 FITNESS VALUE");
            int sum = 0;
            individual minFitness = new individual();
            minFitness.setFitness(Integer.MAX_VALUE);
            for (int i = 0; i < population.size(); i++) {
                sum += population.get(i).getFitness();
                if (population.get(i).getFitness() <= minFitness.getFitness()) {
                    minFitness.setFitness(population.get(i).getFitness());
                    minFitness.setGnome(population.get(i).getGnome());
                }

            }
            
            //for(int i=0;i<population.size();i++){
             //   if(i<9){
              //    System.out.println(" "+(i+1)+" Generation :   "+population.get(i).getGnome()+"      "+population.get(i).getFitness());  
               // }else{
                //     System.out.println((i+1)+" Generation :   "+population.get(i).getGnome()+"      "+population.get(i).getFitness());
               // }
               
            //}
            System.out.println("--------------------------");
            System.out.print("Minimum Value   "+minFitness.getGnome() + "      " + minFitness.getFitness() + "\n");
            System.out.println("Sum of Average Fitness :" + (sum / population.size()));
            long end = System.nanoTime();
            System.out.println("Time : " + (end - start));
            System.out.println("--------------------------");
            gen++;
        }
    }

    private static int rnd(int i, int V) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
