This project is part of the paper [Jin, B., Zhu, W., & Lim, A (2015). Solving the container relocation problem by an improved greedy look-ahead heuristic. [*European Journal of Operational Research*](https://doi.org/10.1016/j.ejor.2014.07.038), 240(3), 837–847].

This project is written in Java 8.

![GLAH](GLAH.png)

The greedy look-ahead heuristic (GLAH) is a three-level meta-heuristic.
- The top level of the GLAH is a *greedy* mechanism: in every stage one relocation is executed to the current layout until the layout becomes empty;
- The middle level, namely the *look-ahead procedure*, applies a limited tree search to give an advice for the main greedy mechanism;
- The bottom level applies a set of specific heuristic rules to conduct the *evaluation* process for every leaf node in the look-ahead tree.

The GLAH and its evaluation heuristic have been referenced in several excellent academic papers. Some authors use them as benchmark methods for algorithm comparison, and some use them as components of sophisticated meta-heuristic algorithms. Our algorithms showcase excellent performance in most cases. Interested readers may refer to the following papers:
- Tricoire, F., Scagnetti, J., & Beham, A. (2018). New insights on the block relocation problem. [*Computers & Operations Research*](https://doi.org/10.1016/j.cor.2017.08.010), 89, 127–139.
- Feillet, D., Parragh, S. N., & Tricoire, F. (2019). A local-search based heuristic for the unrestricted block relocation problem. [*Computers & Operations Research*](https://doi.org/10.1016/j.cor.2019.04.006), 108, 44–56.
- Boge, S. & Knust, S. (2020). The parallel stack loading problem minimizing the number of reshuffles in the retrieval stage. [*European Journal of Operational Research*](https://doi.org/10.1016/j.ejor.2019.08.005), 280(3), 940–952.

Known issue:
- In the evaluation heuristic, the gap utilization process should be carried out to reduce the gap in the assistant stack before executing the assistant GG relocation.
