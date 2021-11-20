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

package algorithm;

import common.Constant;
import common.Container;
import common.Layout;

import java.util.HashMap;
import java.util.Map.Entry;

public class UrgentTargetSelection {
    public static int Nearest_MinLargestAbove = 0, Nearest_MinSmallestAbove = 1, Nearest_MaxLargestAbove = 2,
            Nearest_MaxSmallestAbove = 3, Deepest_MinLargestAbove = 4, Deepest_MinSmallestAbove = 5,
            Deepest_MaxLargestAbove = 6, Deepest_MaxSmallestAbove = 7;

    public static Container getNearestTarget(Layout inst) {
        return getUrgentTarget(inst, 0);
    }

    public static Container getUrgentTarget(Layout inst, int type) {
        int g = inst.nextGroup;
        HashMap<Integer, Container> mark = new HashMap<>();

        for (Container b : inst.containerListOfGroup.get(g)) {
            int n = b.uniqueContainerIndex;
            int s = inst.atStack[n];
            int t = inst.atTier[n];

            if (t < inst.remain - (inst.S - 1) * inst.H) {
                continue;
            }

            if (mark.containsKey(s)) {
                Container p = mark.get(s);
                if (inst.atTier[p.uniqueContainerIndex] < t) {
                    mark.put(s, b);
                }
            } else {
                mark.put(s, b);
            }
        }

        Container r = null;
        int DIST = 0;
        int LARGEST = 0;
        int SMALLEST = 0;

        for (Entry<Integer, Container> en : mark.entrySet()) {
            Container b = en.getValue();
            int n = b.uniqueContainerIndex;
            int s = inst.atStack[n];
            int t = inst.atTier[n];

            int dist = inst.stackHeight[s] - t;

            int largestAbove = 0;
            int smallestAbove = Constant.INF_PRIORITYLABEL;
            for (int j = t + 1; j <= inst.stackHeight[s]; j++) {
                largestAbove = Math.max(largestAbove, inst.bay[s][j].priorityLabel);
                smallestAbove = Math.min(smallestAbove, inst.bay[s][j].priorityLabel);
            }

            if (r == null || winner(type, DIST, LARGEST, SMALLEST, dist, largestAbove, smallestAbove) == 2) {
                r = b;
                DIST = dist;
                LARGEST = largestAbove;
                SMALLEST = smallestAbove;
            }

        }
        return r;
    }

    public static int winner(int type, int dist1, int largestAbove1, int smallestAbove1, int dist2, int largestAbove2,
                             int smallestAbove2) {
        if (dist1 != dist2) {
            if ((type & 4) == 0)// 0, 1, 2, 3
            {
                return (dist1 < dist2 ? 1 : 2);
            } else {
                return (dist1 > dist2 ? 1 : 2);
            }
        } else {
            // Nearest_MinLargestAbove =0,
            // Nearest_MinSmallestAbove=1,
            // Nearest_MaxLargestAbove =2,
            // Nearest_MaxSmallestAbove=3,
            // Deepest_MinLargestAbove =4,
            // Deepest_MinSmallestAbove=5,
            // Deepest_MaxLargestAbove =6,
            // Deepest_MaxSmallestAbove=7;

            int r;
            if ((type & 1) == 0)// 0, 2, 4, 6 LargestAbove
            {
                r = (largestAbove1 < largestAbove2 ? 1 : 2);// min
            } else {
                r = (smallestAbove1 < smallestAbove2 ? 1 : 2);// min
            }
            if ((type & 2) != 0)// max
                r = 3 - r;
            return r;
        }
    }
}
