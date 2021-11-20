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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

public class Lookahead {

    public int ftbg_count;
    public int ntbg_count;
    public int ftbb_count;
    public int ntbb_count;
    public int gg_count;
    public int gb_count;

    public int depthLimit;
    public TreeSet<Integer> hashMap;

    public Lookahead(AlgorithmConfiguration ac) {
        ftbg_count = ac.ftbg_count;
        ntbg_count = ac.ntbg_count;
        ftbb_count = ac.ftbb_count;
        ntbb_count = ac.ntbb_count;
        gg_count = ac.gg_count;

        gb_count = ac.gb_count;
        depthLimit = ac.depthLimit;

    }

    public Operation mostPromisingRelocation(State state) {
        hashMap = new TreeSet<>();
        Operation res = null;
        try {
            Pair<Operation, Integer> pair = getBestBranch(state, 0);
            if (pair != null)
                res = pair.l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private int hashValue(Layout lay, int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(depth);
        for (int i = 1; i <= lay.S; i++) {
            sb.append("[");
            for (int j = 1; j <= lay.stackHeight[i]; j++)
                sb.append(j == 1 ? "" : ",").append(lay.bay[i][j].priorityLabel);
            sb.append("]");
        }
        return sb.toString().hashCode();
    }

    public Pair<Operation, Integer> getBestBranch(State state, int depth) {

        int hash = hashValue(state.inst, depth);
        if (hashMap.contains(hash))
            return null;
        else
            hashMap.add(hash);

        int LBnow = LowerBound.LBFB(state.inst);
        if (LBnow + state.sol.relocationCount >= state.best.relocationCount)
            return null;

        int backup = state.size();

        if (state.isEmpty() || depth >= depthLimit) {

            LinkedList<Operation> saved = state.probing_advices;
            state.probing_advices = null;

            if (!state.isEmpty())
                Probing.evaluationHeuristic(state);
            Pair<Operation, Integer> laa = new Pair<>(null, state.sol.relocationCount);

            while (state.size() > backup)
                state.undo();
            state.probing_advices = saved;

            return laa;
        } else {
            Pair<Operation, Integer> res = null;

            ArrayList<Operation> list = allAvailableRelocations(state);

            if (state.probing_advices == null) {
                Probing.evaluationHeuristic(state);
                LinkedList<Operation> temp = new LinkedList<>();
                while (state.size() > backup) {
                    Operation lastOp = state.sol.getLastOperation();
                    if (lastOp.isRelocation())
                        temp.addFirst(new Operation(lastOp.container, lastOp.from, lastOp.to, true));
                    state.undo();
                }

                state.probing_advices = temp;
            }

            LinkedList<Operation> saved = state.probing_advices;

            Operation prob_advice = state.probing_advices.getFirst();
            list.add(0, prob_advice);

            for (Operation op : list) {
                state.goOneStep(op);
                state.tryRetrievals();

                Pair<Operation, Integer> next = getBestBranch(state, depth + 1);

                if (next != null && (res == null || res.r > next.r))
                    res = new Pair<>(op, next.r);

                while (state.size() > backup)
                    state.undo();
                state.probing_advices = saved;

                if (op.is_probing_advice) {
                    state.probing_advices.addFirst(op);
                }
            }
            return res;
        }
    }

    public ArrayList<Operation> allAvailableRelocations(State state) {

        ArrayList<Pair<Operation, Integer>> ftbg = new ArrayList<>();
        ArrayList<Pair<Operation, Integer>> ntbg = new ArrayList<>();
        ArrayList<Pair<Operation, Integer>> ftbb = new ArrayList<>();
        ArrayList<Pair<Operation, Integer>> ntbb = new ArrayList<>();
        ArrayList<Pair<Operation, Integer>> gg = new ArrayList<>();
        ArrayList<Pair<Operation, Integer>> gb = new ArrayList<>();

        Layout inst = state.inst;

        for (int i = 1; i <= inst.S; i++) {
            if (inst.stackHeight[i] == 0)
                continue;
            Placement p1;
            if (inst.isTopWellPlaced(i))
                p1 = Placement.G;
            else if (inst.containTarget(i))
                p1 = Placement.FTB;
            else
                p1 = Placement.NTB;

            Container con = inst.topContainer(i);
            boolean emptyTrial = false;
            for (int j = 1; j <= inst.S; j++) {
                if (j == i || inst.stackHeight[j] == inst.H || (inst.stackHeight[j] == 0 && emptyTrial))
                    continue;

                if (inst.stackHeight[j] == 0)
                    emptyTrial = true;
                Placement p2;
                if (con.priorityLabel <= inst.supportCapacity(j))
                    p2 = Placement.G;
                else
                    p2 = Placement.B;

                Operation op = new Operation(con, i, j, false);
                if (p1 == Placement.FTB && p2 == Placement.G) {
                    int score = inst.supportCapacity(j) - con.priorityLabel;
                    ftbg.add(new Pair<>(op, score));
                } else if (p1 == Placement.NTB && p2 == Placement.G) {
                    int score = inst.supportCapacity(j) - con.priorityLabel;
                    ntbg.add(new Pair<>(op, score));
                } else if (p1 == Placement.FTB) { // p2 == Placement.B
                    int score = con.priorityLabel - inst.supportCapacity(j);
                    ftbb.add(new Pair<>(op, score));
                } else if (p1 == Placement.NTB) { // p2 == Placement.B
                    int score = con.priorityLabel - inst.supportCapacity(j);
                    ntbb.add(new Pair<>(op, score));

                } else if (p2 == Placement.G) {
                    int score = inst.supportCapacity(j) - inst.supportCapacityExceptTop(i);
                    gg.add(new Pair<>(op, score));
                } else {
                    int score = -inst.supportCapacity(j) - inst.supportCapacityExceptTop(i);
                    gb.add(new Pair<>(op, score));
                }
            }

        }
        Collections.sort(ftbg);
        Collections.sort(ntbg);
        Collections.sort(ftbb);
        Collections.sort(ntbb);
        Collections.sort(gg);
        Collections.sort(gb);

        ArrayList<Operation> list = new ArrayList<>();
        for (int i = 0; i < ftbg_count && i < ftbg.size(); i++) {
            list.add(ftbg.get(i).l);
        }
        for (int i = 0; i < ntbg_count && i < ntbg.size(); i++) {
            list.add(ntbg.get(i).l);
        }
        for (int i = 0; i < ftbb_count && i < ftbb.size(); i++) {
            list.add(ftbb.get(i).l);
        }
        for (int i = 0; i < ntbb_count && i < ntbb.size(); i++) {
            list.add(ntbb.get(i).l);
        }
        for (int i = 0; i < gg_count && i < gg.size(); i++) {
            list.add(gg.get(i).l);
        }
        for (int i = 0; i < gb_count && i < gb.size(); i++) {
            list.add(gb.get(i).l);
        }
        return list;
    }

    private enum Placement {
        FTB, NTB, B, G
    }
}
