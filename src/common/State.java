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

import algorithm.UrgentTargetSelection;

import java.util.LinkedList;

public class State {

    public LinkedList<Operation> probing_advices;

    public Layout inst;
    public Solution sol;
    public Solution best;

    public void undo() {
        Operation move = sol.removeLastOperation();
        inst.undoMove(move);

    }

    public void goOneStep(Operation move) {
        if (move.is_probing_advice) {
            try {
                Operation nex = probing_advices.getFirst();
                if (!move.equalTo(nex))
                    throw new Exception("NOT THE SAME ADVICE!!");
            } catch (Exception e) {
                System.err.println(e);
            }
            probing_advices.removeFirst();
        } else if (move.isRelocation()) {
            probing_advices = null;
        }
        sol.append(move);
        inst.doMove(move);
    }

    public boolean isEmpty() {
        return inst.isEmpty();
    }

    public int tryRetrievals() {
        if (isEmpty())
            return 0;
        Container pivotBlock = UrgentTargetSelection.getNearestTarget(inst);
        int n = pivotBlock.uniqueContainerIndex;

        int pivotTier = inst.atTier[n];
        int pivotStack = inst.atStack[n];

        int count = 0;
        while (pivotTier == inst.stackHeight[pivotStack]) {
            Operation remove = new Operation(pivotBlock, pivotStack, 0, false);

            goOneStep(remove);

            count++;

            if (isEmpty())
                break;
            pivotBlock = UrgentTargetSelection.getNearestTarget(inst);
            n = pivotBlock.uniqueContainerIndex;

            pivotTier = inst.atTier[n];
            pivotStack = inst.atStack[n];

        }

        return count;
    }

    public int tryRetrievals(int s) {
        int count = 0;
        while (inst.stackHeight[s] > 0 && inst.topContainer(s).priorityLabel == inst.nextGroup) {
            Operation retrieval = new Operation(inst.topContainer(s), s, 0, false);

            goOneStep(retrieval);

            count++;
        }
        return count;
    }

    public boolean updateBest() {
        Solution rs = new SolutionReducer(inst.N).reduce(sol);
        if (best == null || best.relocationCount > rs.relocationCount) {
            best = rs;
            return true;
        } else
            return false;
    }

    public int size() {
        return sol.operations.size();
    }
}
