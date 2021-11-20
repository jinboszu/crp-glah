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

public class Operation {
    public Container container;
    public int from;
    public int to;
    public String name;
    public boolean is_probing_advice;

    public boolean equalTo(Operation x) {
        return x.container.uniqueContainerIndex == container.uniqueContainerIndex && x.from == from && x.to == to;
    }

    public Operation(Container c, int f, int t, boolean prob) {
        container = c;
        from = f;
        to = t;
        is_probing_advice = prob;
    }

    public boolean isRetrieval() {
        return to == 0;
    }

    public boolean isRelocation() {
        return to != 0;
    }

    public String toString() {
        return "" + container.priorityLabel + "(" + container.uniqueContainerIndex + ") : " + from + "->" + to;
    }
}
