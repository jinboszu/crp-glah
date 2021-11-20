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

public class Solution {

    public ArrayList<Operation> operations;
    public int relocationCount;
    public int retrievalCount;

    public Solution() {
        operations = new ArrayList<>();
        relocationCount = 0;
        retrievalCount = 0;
    }

    public void append(Operation move) {
        operations.add(move);
        if (move.isRelocation())
            relocationCount++;
        else
            retrievalCount++;
    }

    public Operation getLastOperation() {
        return operations.get(operations.size() - 1);
    }

    public Operation removeLastOperation() {
        if (operations.get(operations.size() - 1).isRelocation())
            relocationCount--;
        else
            retrievalCount--;
        return operations.remove(operations.size() - 1);
    }

    public Solution copy() {
        Solution c = new Solution();
        for (Operation move : operations)
            c.append(move);
        c.relocationCount = relocationCount;
        c.retrievalCount = retrievalCount;
        return c;
    }

    public String toString() {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < operations.size(); i++) {
            if (i != 0)
                r.append(" ,");
            r.append("[").append(operations.get(i)).append("]");
        }
        return r.toString();
    }

}
