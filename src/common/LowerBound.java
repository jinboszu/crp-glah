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

public class LowerBound {
    public static int LBFB(Layout inst) {

        State state = new State();
        state.inst = inst;
        state.sol = new Solution();
        state.tryRetrievals();
        // the non-BG relocation = 1 or 0

        int minTop = Constant.INF_PRIORITYLABEL;
        int maxMin = 0;
        boolean noemptystack = true;

        int badBlockCount = inst.badCount;
        for (int s = 1; s <= inst.S && noemptystack; s++) {
            if (inst.stackHeight[s] == 0) {
                noemptystack = false;
            } else {
                Container topBlock = inst.topContainer(s);
                minTop = Math.min(minTop, topBlock.priorityLabel);
            }

            int min = inst.supportCapacity(s);
            maxMin = Math.max(maxMin, min);

        }

        while (state.sol.operations.size() > 0)
            state.undo();

        int Forster_Bortfeldt = (noemptystack && minTop > maxMin ? 1 : 0);

        return badBlockCount + Forster_Bortfeldt;
    }

    public static int LB1(Layout inst) {
        return inst.badCount;
    }

    public static int LB3(Layout inst) {
        int badBlockCount = 0;
        for (int s = 1; s <= inst.S; s++) {
            int currentMin = Constant.FLOOR_PRIORITYLABEL;

            for (int t = 1; t <= inst.stackHeight[s]; t++) {
                int cur = inst.bay[s][t].priorityLabel;

                if (cur > currentMin)
                    badBlockCount++;

                if (currentMin > cur)
                    currentMin = cur;
            }

        }
        return badBlockCount;
    }
}
