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

import algorithm.AlgorithmConfiguration;
import algorithm.MainProcess;
import common.Layout;
import common.SolutionReport;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Scanner;

public class Executor {
    /*
     * args: 0: BF or CVS or ZHU 1: 3,6-8,14 or ALL 2: PU2 or probe 3:
     * reportName
     */

    public static String[] AUTHORS = {"TEST", "BF", "CVS", "CVS-msh", "Zhu"};
    public static int[] testcount = {1, 32, 21, 21, 25};
    public static String reportName = "GLAH";

    public static void main(String[] args) throws Exception {
        String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        new File("result/" + date).mkdirs();

        AlgorithmConfiguration[] acs = new AlgorithmConfiguration[2];

        acs[0] = new AlgorithmConfiguration();
        acs[0].depthLimit = 3;
        acs[0].ftbg_count = 5;
        acs[0].ntbg_count = 5;
        acs[0].ftbb_count = 3;
        acs[0].ntbb_count = 3;
        acs[0].gg_count = 1;
        acs[0].gb_count = 1;

        acs[1] = new AlgorithmConfiguration();
        acs[1].depthLimit = 4;
        acs[1].ftbg_count = 5;
        acs[1].ntbg_count = 5;
        acs[1].ftbb_count = 3;
        acs[1].ntbb_count = 3;
        acs[1].gg_count = 1;
        acs[1].gb_count = 1;

        for (int whoseData = 3; whoseData <= 4; whoseData++) {
            for (AlgorithmConfiguration ac : acs) {
                String author = AUTHORS[whoseData];

                File result = new File("result/" + date + "/" + reportName + "-" + AUTHORS[whoseData] + " D="
                                               + ac.depthLimit + ".csv");
                File sol = new File("result/" + date + "/" + reportName + "-" + AUTHORS[whoseData] + " D="
                                            + ac.depthLimit + ".txt");

                PrintStream ps = new PrintStream(result);
                PrintStream sw = new PrintStream(sol);
                ps.println("Group,Case,LBFB,IS,Res,Time");
                for (int testgroup = 1; testgroup <= testcount[whoseData]; testgroup++) {
                    File dir = new File("data/" + author + String.format("%02d", testgroup) + "/");
                    File[] files = dir.listFiles();
                    Arrays.sort(files, Comparator.comparing(File::getName));

                    int testcasesum = 0;
                    double timesum = 0;
                    for (int testcase = 1; testcase <= files.length; testcase++) {

                        File file = files[testcase - 1];
                        Scanner scn = new Scanner(file);
                        Layout inst = new Layout(scn);

                        MainProcess mp = new MainProcess(inst, ac);

                        SolutionReport s = mp.solve1();

                        ps.println(testgroup + "," + testcase + "," + s.lowerBound + ","
                                           + s.initialSolution.relocationCount + "," + s.bestEverFound.relocationCount + ","
                                           + 0.001 * s.timeUsed);
                        sw.println(testgroup + " " + testcase);
                        sw.println(s.bestEverFound);
                        testcasesum += s.bestEverFound.relocationCount;
                        timesum += 0.001 * s.timeUsed;
                    }
                    String time = String.format("%.1fs", timesum / files.length);
                    System.out.println(author + " Group " + testgroup + " = " + testcasesum * 1.0 / files.length
                                               + ", time= " + time);
                }
                ps.close();
                sw.close();
            }
        }

    }
}
