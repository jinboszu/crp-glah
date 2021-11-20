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

import common.*;

public class Probing {

    public static Container getUrgentTarget(Layout inst) {
        return UrgentTargetSelection.getUrgentTarget(inst, UrgentTargetSelection.Nearest_MinLargestAbove);
    }

    public static boolean notIn(int a, int[] as) {
        for (int v : as)
            if (a == v)
                return false;
        return true;
    }

    public static int findSupportStackMinCapacity(Layout inst, int label, int[] without) {
        int minCapacity = Constant.INF_PRIORITYLABEL;
        int r = -1;
        for (int s = 1; s <= inst.S; s++) {
            if (notIn(s, without) && inst.stackHeight[s] < inst.H) {
                int cap = inst.supportCapacity(s);

                if (cap >= label && minCapacity > cap) {
                    r = s;
                    minCapacity = cap;
                }
            }
        }
        return r;

    }

    public static int findStackMaxCapacity(Layout inst, int[] without) {
        int max = 0;
        int r = -1;
        for (int s = 1; s <= inst.S; s++) {

            if (notIn(s, without) && inst.stackHeight[s] < inst.H && max < inst.supportCapacity(s)) {
                r = s;
                max = inst.supportCapacity(s);
            }
        }
        return r;

    }

    public static void evaluationHeuristic(State state) {
        // Actually, the argument ``state'' comes here, is not retrievable if it
        // is passed by lookahead
        // only the raw data ``state'' has the possibility to be retrievable
        // here
        state.tryRetrievals();

        Layout inst = state.inst;
        // no retrieval can be done
        while (!inst.isEmpty()) {
            Container ut = getUrgentTarget(inst);
            int utIndex = ut.uniqueContainerIndex;
            int utHeight = inst.atTier[utIndex];
            int utStack = inst.atStack[utIndex];

            while (utHeight < inst.stackHeight[utStack]) {

                Container c = inst.topContainer(utStack);

                int pStack = findSupportStackMinCapacity(inst, c.priorityLabel, new int[]{utStack});

                if (pStack == -1) {
                    // c can not be supported onto any stack
                    // we try to find vacating stack
                    Container cv = null;
                    int largestcv = 0;
                    int vStack = -1;
                    for (int i = 1; i <= inst.S; i++) {
                        if (i == utStack)
                            continue;
                        Container itop = inst.topContainer(i);

                        if (inst.supportCapacityExceptTop(i) >= itop.priorityLabel
                                && inst.supportCapacityExceptTop(i) >= c.priorityLabel) {
                            int j = findSupportStackMinCapacity(inst, itop.priorityLabel, new int[]{utStack, i});
                            if (j != -1 && itop.priorityLabel > largestcv) {
                                largestcv = itop.priorityLabel;
                                pStack = i;
                                vStack = j;
                                cv = itop;
                            }
                        }
                    }

                    if (pStack != -1) {
                        // feasible (one step) vacating is found
                        Operation vOp = new Operation(cv, pStack, vStack, false);
                        state.goOneStep(vOp);

                        // if pStack.topContainer is a target
                        // in this case, it's impossible
                        // because this vacating is a one-container-GG vacating
                        // state.tryRetrievals(pStack);

                    } else {
                        // no feasible (one step) vacating is found
                        pStack = findStackMaxCapacity(inst, new int[]{utStack});
                        if (inst.stackHeight[pStack] == inst.H - 1 &&
                                inst.stackHeight[utStack] - utHeight >= 2) {
                            int min = Constant.INF_PRIORITYLABEL;
                            for (int j = utHeight + 1; j <= inst.stackHeight[utStack]; j++)
                                min = Math.min(min, inst.bay[utStack][j].priorityLabel);
                            if (c.priorityLabel != min) {
                                pStack = findStackMaxCapacity(inst, new int[]{utStack, pStack});
                            }

                        }
                    }
                }

                // do interlaying relocations
                while (c.priorityLabel <= inst.supportCapacity(pStack) && inst.stackHeight[pStack] <= inst.H - 2) {

                    int LARGEST = 0;
                    int I = -1;
                    for (int i = 1; i <= inst.S; i++) {
                        if (i == pStack || i == utStack || inst.stackHeight[i] == 0)
                            continue;
                        int pri = inst.topContainer(i).priorityLabel;
                        if (!inst.isTopWellPlaced(i) && c.priorityLabel <= pri
                                && pri <= inst.supportCapacity(pStack) && LARGEST < pri) {
                            LARGEST = pri;
                            I = i;
                        }
                    }
                    if (I != -1) {
                        Operation sOp = new Operation(inst.topContainer(I), I, pStack, false);

                        state.goOneStep(sOp);

                        // in case of sStack.topContainer is a target
                        state.tryRetrievals(I);
                    } else
                        break;
                }

                Operation op = new Operation(c, utStack, pStack, false);

                state.goOneStep(op);
            }
            // System.err.println(state.inst);
            state.tryRetrievals();

        }

        state.updateBest();

    }

    public static void PU2(State state) {

        // Actually, the argument ``state'' comes here, is not retrievable if it
        // is passed by lookahead
        // only the raw data ``state'' has the possibility to be retrievable
        // here
        state.tryRetrievals();

        Layout inst = state.inst;
        // no retrieval can be done
        while (!inst.isEmpty()) {
            Container ut = getUrgentTarget(inst);
            int utIndex = ut.uniqueContainerIndex;
            int utHeight = inst.atTier[utIndex];
            int utStack = inst.atStack[utIndex];

            while (utHeight < inst.stackHeight[utStack]) {

                Container c = inst.topContainer(utStack);

                int pStack = findSupportStackMinCapacity(inst, c.priorityLabel, new int[]{utStack});

                if (pStack == -1) {
                    // c can not be supported onto any stack
                    // we try to find vacating stack
                    Container cv = null;
                    int largestcv = 0;
                    int vStack = -1;
                    for (int i = 1; i <= inst.S; i++) {
                        if (i == utStack)
                            continue;
                        Container itop = inst.topContainer(i);

                        if (inst.supportCapacityExceptTop(i) >= itop.priorityLabel
                                && inst.supportCapacityExceptTop(i) >= c.priorityLabel) {
                            int j = findSupportStackMinCapacity(inst, itop.priorityLabel, new int[]{utStack, i});
                            if (j != -1 && itop.priorityLabel > largestcv) {
                                largestcv = itop.priorityLabel;
                                pStack = i;
                                vStack = j;
                                cv = itop;
                            }
                        }
                    }

                    if (pStack != -1) {
                        // feasible (one step) vacating is found
                        Operation vOp = new Operation(cv, pStack, vStack, false);
                        state.goOneStep(vOp);

                        // if pStack.topContainer is a target
                        // in this case, it's impossible
                        // because this vacating is a one-container-GG vacating
                        // state.tryRetrievals(pStack);

                    } else {
                        // no feasible (one step) vacating is found
                        pStack = findStackMaxCapacity(inst, new int[]{utStack});

                        if (inst.stackHeight[pStack] == inst.H - 1 && inst.stackHeight[utStack] - utHeight >= 2) {
                            int min = Constant.INF_PRIORITYLABEL;
                            for (int j = utHeight + 1; j <= inst.stackHeight[utStack]; j++)
                                min = Math.min(min, inst.bay[utStack][j].priorityLabel);
                            if (c.priorityLabel != min) {
                                pStack = findStackMaxCapacity(inst, new int[]{utStack, pStack});
                            }

                        }
                    }
                }

                Operation op = new Operation(c, utStack, pStack, false);

                state.goOneStep(op);
            }
            // System.err.println(state.inst);
            state.tryRetrievals();

        }

        state.updateBest();

    }

}
