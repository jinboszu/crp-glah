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

public class MainProcess {

    public MainProcess(Layout inst, AlgorithmConfiguration ac) {
        instance = inst;
        this.ac = ac;
    }

    public AlgorithmConfiguration ac;
    public Layout instance;

    public SolutionReport solve1() {
        long startTime = System.currentTimeMillis();

        int LB = LowerBound.LBFB(instance);

        State state = new State();
        state.inst = instance.copy();
        state.sol = new Solution();
        Probing.evaluationHeuristic(state);
        Solution IS = state.best.copy();

        state.inst = instance.copy();
        state.sol = new Solution();

        // best is always updated automaticly at the end of probing

        Lookahead la = new Lookahead(ac);

        // raw data must do this first before going into lookahead
        state.tryRetrievals();
        while (!state.isEmpty()) {
            int LBnow = LowerBound.LBFB(state.inst);

            if (LBnow + state.sol.relocationCount >= state.best.relocationCount)
                break;

            Operation op = la.mostPromisingRelocation(state);
            if (op == null)
                break;
            state.goOneStep(op);
            state.tryRetrievals();
        }

        SolutionReport r = new SolutionReport();

        r.bestEverFound = state.best;

        r.lowerBound = LB;
        r.initialSolution = IS;
        r.timeUsed = System.currentTimeMillis() - startTime;
        return r;

    }

}
