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

import java.util.Arrays;

public class SolutionReducer {
    public int N;

    public SolutionReducer(int n2) {
        N = n2;
    }

    public Solution reduce(Solution s) {
        Solution ns = new Solution();

        int[] lastOp = new int[N + 1];
        Arrays.fill(lastOp, -1);
        for (int i = 0; i < s.operations.size(); i++) {
            Operation op = s.operations.get(i);
            if (op.isRetrieval()) {
                ns.append(op);
            } else {
                int n = op.container.uniqueContainerIndex;
                if (lastOp[n] == -1) {
                    lastOp[n] = ns.operations.size();
                    ns.append(op);
                } else {
                    int to2 = op.to;
                    Operation pre = ns.operations.get(lastOp[n]);

                    boolean to2appear = false;
                    for (int j = lastOp[n] + 1; j < ns.operations.size(); j++) {
                        Operation mid = ns.operations.get(j);
                        if (mid.from == to2 || mid.to == to2) {
                            to2appear = true;
                            break;
                        }
                    }
                    if (to2appear) {
                        lastOp[n] = ns.operations.size();
                        ns.append(op);
                    } else {
                        Operation comb = new Operation(op.container, pre.from, to2, false);
                        comb.name = "Combination";
                        ns.operations.set(lastOp[n], comb);
                    }
                }
            }
        }
        return ns;
    }
}
