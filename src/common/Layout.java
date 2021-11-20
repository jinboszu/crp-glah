/*
 * This program is part of the paper "Solving the container relocation
 * problem by an improved greedy look-ahead heuristic".
 *
 * Copyright (c) 2015 Bo Jin <jinbostar@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package common;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class Layout {
    public int S;
    public int H;
    public int N;
    public int G;

    public int[] groupOf;

    public Container[][] bay;
    public int[] stackHeight;
    public int[][] minUnderInclusive;

    public int[] atStack;
    public int[] atTier;

    public ArrayList<TreeSet<Container>> containerListOfGroup;
    public int nextGroup;

    public int remain;
    public int badCount;

    // TT only
    public Layout(Scanner scn, int T) {
        String[] str;
        scn.nextLine();
        scn.nextLine();

        S = scn.nextInt();
        H = T;
        N = scn.nextInt();

        groupOf = new int[N + 1];
        bay = new Container[S + 1][H + 1];
        stackHeight = new int[S + 1];
        minUnderInclusive = new int[S + 1][H + 1];
        atStack = new int[N + 1];
        atTier = new int[N + 1];
        remain = N;
        badCount = 0;
        G = 0;
        int n = 0;
        for (int s = 1; s <= S; s++) {
            stackHeight[s] = scn.nextInt();
            // assert(stackHeight[s]<=H);

            minUnderInclusive[s][0] = Constant.FLOOR_PRIORITYLABEL;

            for (int t = 1; t <= stackHeight[s]; t++) {
                n++;
                int g = scn.nextInt();
                if (G < g)
                    G = g;
                Container block = new Container(g, n);
                groupOf[n] = g;
                bay[s][t] = block;
                minUnderInclusive[s][t] = Math.min(g, minUnderInclusive[s][t - 1]);
                if (minUnderInclusive[s][t - 1] < g)
                    badCount++;
                atStack[n] = s;
                atTier[n] = t;
            }
        }
        // assert(n==N);

        containerListOfGroup = new ArrayList<>();
        for (int g = 0; g <= G; g++)
            containerListOfGroup.add(new TreeSet<>());
        for (int s = 1; s <= S; s++) {
            for (int t = 1; t <= stackHeight[s]; t++) {
                int g = bay[s][t].priorityLabel;

                containerListOfGroup.get(g).add(bay[s][t]);
            }
        }
        nextGroup = 1;
        while (nextGroup <= G && containerListOfGroup.get(nextGroup).isEmpty())
            nextGroup++;
    }

    public Layout copy() {
        Layout inst = new Layout();
        inst.S = S;
        inst.H = H;
        inst.N = N;
        inst.G = G;
        inst.groupOf = groupOf;
        inst.bay = new Container[S + 1][H + 1];
        inst.minUnderInclusive = new int[S + 1][H + 1];
        for (int i = 1; i <= S; i++) {
            inst.bay[i] = bay[i].clone();
            inst.minUnderInclusive[i] = minUnderInclusive[i].clone();

        }
        inst.stackHeight = stackHeight.clone();
        inst.atStack = atStack.clone();
        inst.atTier = atTier.clone();
        inst.containerListOfGroup = new ArrayList<>();
        for (int g = 0; g <= G; g++) {
            TreeSet<Container> ts = new TreeSet<>();
            ts.addAll(containerListOfGroup.get(g));
            inst.containerListOfGroup.add(ts);
        }

        inst.nextGroup = nextGroup;
        inst.remain = remain;
        inst.badCount = badCount;
        return inst;
    }

    public Layout() {

    }

    public Layout(Scanner scn) {
        String[] str;
        scn.nextLine();
        str = scn.nextLine().split(" ");
        S = Integer.parseInt(str[2]);
        str = scn.nextLine().split(" ");
        H = Integer.parseInt(str[2]);
        str = scn.nextLine().split(" ");
        N = Integer.parseInt(str[2]);

        groupOf = new int[N + 1];
        bay = new Container[S + 1][H + 1];
        stackHeight = new int[S + 1];
        minUnderInclusive = new int[S + 1][H + 1];
        atStack = new int[N + 1];
        atTier = new int[N + 1];
        remain = N;
        badCount = 0;
        G = 0;
        int n = 0;
        for (int s = 1; s <= S; s++) {
            str = scn.nextLine().split(" ");
            stackHeight[s] = str.length - 3;
            // assert(stackHeight[s]<=H);

            minUnderInclusive[s][0] = Constant.FLOOR_PRIORITYLABEL;

            for (int i = 3; i < str.length; i++) {
                int t = i - 2;
                n++;
                int g = Integer.parseInt(str[i]);
                if (G < g)
                    G = g;
                Container block = new Container(g, n);
                groupOf[n] = g;
                bay[s][t] = block;
                minUnderInclusive[s][t] = Math.min(g, minUnderInclusive[s][t - 1]);
                if (minUnderInclusive[s][t - 1] < g)
                    badCount++;
                atStack[n] = s;
                atTier[n] = t;
            }
        }
        // assert(n==N);

        containerListOfGroup = new ArrayList<>();
        for (int g = 0; g <= G; g++)
            containerListOfGroup.add(new TreeSet<>());
        for (int s = 1; s <= S; s++) {
            for (int t = 1; t <= stackHeight[s]; t++) {
                int g = bay[s][t].priorityLabel;

                containerListOfGroup.get(g).add(bay[s][t]);
            }
        }
        nextGroup = 1;
        while (nextGroup <= G && containerListOfGroup.get(nextGroup).isEmpty())
            nextGroup++;
    }

    public void printBay() {

        for (int t = H; t > 0; t--) {
            System.err.printf("%2d|", t);
            for (int s = 1; s <= S; s++) {
                if (bay[s][t] != null) {
                    System.err.printf(" %2d", bay[s][t].priorityLabel);
                } else {
                    System.err.print("   ");
                }
            }
            System.err.print("\n");
        }

        System.err.print("--+");
        for (int s = 1; s <= S; s++) {
            System.err.print("---");
        }
        System.err.print("\n");

        System.err.print("  |");
        for (int s = 1; s <= S; s++) {
            System.err.printf(" %2d", s);
        }
        System.err.print("\n");
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int t = H; t > 0; t--) {
            str.append(String.format("%2d|", t));
            for (int s = 1; s <= S; s++) {
                if (bay[s][t] != null) {
                    str.append(String.format(" %2d", bay[s][t].priorityLabel));
                } else {
                    str.append("   ");
                }
            }
            str.append("\n");

        }

        str.append("--+");
        str.append("---".repeat(Math.max(0, S)));
        str.append("\n");

        str.append("  |");
        for (int s = 1; s <= S; s++) {
            str.append(String.format(" %2d", s));
        }
        str.append("\n");
        return str.toString();
    }

    public void printMin() {

        for (int t = H; t > 0; t--) {
            System.err.printf("%d|", t);
            for (int s = 1; s <= S; s++) {
                if (bay[s][t] != null) {
                    System.err.printf(" %2d", minUnderInclusive[s][t]);
                } else {
                    System.err.print("   ");
                }
            }
            System.err.print("\n");
        }

        System.err.print("-+");
        for (int s = 1; s <= S; s++) {
            System.err.print("---");
        }
        System.err.print("\n");

        System.err.print(" |");
        for (int s = 1; s <= S; s++) {
            System.err.printf(" %2d", s);
        }
        System.err.print("\n");
    }

    public boolean isEmpty() {
        return remain == 0;
    }

    public void doMove(Operation move) {
        Container block = move.container;

        int g = block.priorityLabel;
        int n = block.uniqueContainerIndex;
        int s = move.from;

        if (isBadlyPlaced(s, stackHeight[s]))
            badCount--;

        int t = stackHeight[s]--;

        minUnderInclusive[s][t] = 0;
        bay[s][t] = null;

        if (move.isRetrieval()) {
            remain--;
            atStack[n] = -atStack[n];
            atTier[n] = -atTier[n];

            containerListOfGroup.get(g).remove(block);
            while (nextGroup <= G && containerListOfGroup.get(nextGroup).isEmpty())
                nextGroup++;

        } else {
            int s2 = move.to;
            int t2 = ++stackHeight[s2];
            bay[s2][t2] = block;
            minUnderInclusive[s2][t2] = g;
            if (minUnderInclusive[s2][t2 - 1] < minUnderInclusive[s2][t2])
                minUnderInclusive[s2][t2] = minUnderInclusive[s2][t2 - 1];

            atStack[n] = s2;
            atTier[n] = t2;

            if (isBadlyPlaced(s2, t2))
                badCount++;
        }
    }

    public void undoMove(Operation move) {
        Container block = move.container;
        int g = block.priorityLabel;
        int n = block.uniqueContainerIndex;
        int s = move.from;

        if (move.isRetrieval()) {
            remain++;
            containerListOfGroup.get(g).add(block);
            nextGroup = g;
        } else {
            int s2 = move.to;

            if (isBadlyPlaced(s2, stackHeight[s2]))
                badCount--;

            int t2 = stackHeight[s2]--;
            minUnderInclusive[s2][t2] = 0;
            bay[s2][t2] = null;
        }

        int t = ++stackHeight[s];
        bay[s][t] = block;
        minUnderInclusive[s][t] = g;
        if (minUnderInclusive[s][t - 1] < minUnderInclusive[s][t])
            minUnderInclusive[s][t] = minUnderInclusive[s][t - 1];

        atStack[n] = s;
        atTier[n] = t;

        if (isBadlyPlaced(s, t))
            badCount++;

    }

    public int supportCapacity(int s) {
        int t = stackHeight[s];
        return minUnderInclusive[s][t];
    }

    public int slotSupportCapacity(int s, int t) {
        return minUnderInclusive[s][t - 1];
    }

    public int supportCapacityExceptTop(int s) {
        int t = stackHeight[s] - 1;
        return minUnderInclusive[s][t];
    }

    public boolean isBadlyPlaced(int i, int j) {
        Container b = bay[i][j];
        return b.priorityLabel != minUnderInclusive[i][j];
    }

    public boolean isTopWellPlaced(int i) {
        int j = stackHeight[i];
        Container b = bay[i][j];
        return b.priorityLabel == minUnderInclusive[i][j];
    }

    public boolean containTarget(int s) {
        return nextGroup == supportCapacity(s);
    }

    public Container topContainer(int s) {
        if (stackHeight[s] == 0)
            return null;

        return bay[s][stackHeight[s]];
    }
}
