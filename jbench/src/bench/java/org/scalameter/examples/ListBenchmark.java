package org.scalameter.examples;

import java.util.*;
import org.scalameter.Context;
import org.scalameter.japi.ContextBuilder;
import org.scalameter.japi.Fun1;
import org.scalameter.japi.JBench;
import org.scalameter.japi.JGen;
import org.scalameter.japi.annotation.*;
import scala.Tuple2;


public class ListBenchmark extends JBench.OnlineRegressionReport {
  public final JGen<Integer> sizes = JGen.exponential("size", 100, 10000, 10);

  // on java 8 `map` combinator can be expressed using lambdas
  public final JGen<Tuple2<Integer, ArrayList<Integer>>> arrayLists = sizes.zip(
      sizes.map(new Fun1<Integer, ArrayList<Integer>>() {
        public ArrayList<Integer> apply(Integer v) {
          return new ArrayList<Integer>();
        }
      })
  );

  // on java 8 `map` combinator can be expressed using lambdas
  public final JGen<Tuple2<Integer, LinkedList<Integer>>> linkedLists = sizes.zip(
      sizes.map(new Fun1<Integer, LinkedList<Integer>>() {
        public LinkedList<Integer> apply(Integer v) {
          return new LinkedList<Integer>();
        }
      })
  );

  @Override
  public Context defaultConfig() {
    return new ContextBuilder()
        .put("exec.benchRuns", 25)
        .put("exec.independentSamples", 2)
        .build();
  }

  public void arrayListSetup(Tuple2<Integer, ArrayList<Integer>> v) {
    int size = v._1();
    ArrayList<Integer> list = v._2();

    Random random = new Random(size);
    for (int i = 0; i < size; i++) {
      list.add(random.nextInt());
    }
  }

  public void linkedListSetup(Tuple2<Integer, LinkedList<Integer>> v) {
    int size = v._1();
    LinkedList<Integer> list = v._2();

    Random random = new Random(size);
    for (int i = 0; i < size; i++) {
      list.add(random.nextInt());
    }
  }

  @gen("arrayLists")
  @setup("arrayListSetup")
  @benchmark("lists.forloops")
  @curve("array")
  public int arrayListSum(Tuple2<Integer, ArrayList<Integer>> v) {
    ArrayList<Integer> list = v._2();
    int result = 0;

    for (Integer elem : list) {
      result += elem;

    }
    return result;
  }

  @gen("linkedLists")
  @setup("linkedListSetup")
  @benchmark("lists.forloops")
  @curve("linked")
  public int linkedListSum(Tuple2<Integer, LinkedList<Integer>> v) {
    LinkedList<Integer> list = v._2();
    int result = 0;

    for (Integer elem : list) {
      result += elem;

    }
    return result;
  }

  @gen("arrayLists")
  @benchmark("lists.ops.add")
  @curve("array")
  public ArrayList<Integer> arrayListAdd(Tuple2<Integer, ArrayList<Integer>> v) {
    int size = v._1();
    ArrayList<Integer> list = v._2();

    Random random = new Random(size);
    for (int i = 0; i < size; i++) {
      list.add(random.nextInt());
    }

    return list;
  }

  @gen("linkedLists")
  @benchmark("lists.ops.add")
  @curve("linked")
  public LinkedList<Integer> linkedListAdd(Tuple2<Integer, LinkedList<Integer>> v) {
    int size = v._1();
    LinkedList<Integer> list = v._2();

    Random random = new Random(size);
    for (int i = 0; i < size; i++) {
      list.add(random.nextInt());
    }

    return list;
  }

  @gen("arrayLists")
  @setup("arrayListSetup")
  @benchmark("lists.ops.remove")
  @curve("array")
  public ArrayList<Integer> arrayListRemove(Tuple2<Integer, ArrayList<Integer>> v) {
    int size = v._1();
    ArrayList<Integer> list = v._2();

    Random random = new Random(size);
    Collections.shuffle(list, random);
    random = new Random(size);
    for (int i = 0; i < size; i++) {
      list.remove((Integer) random.nextInt());
    }

    return list;
  }

  @gen("linkedLists")
  @setup("linkedListSetup")
  @benchmark("lists.ops.remove")
  @curve("linked")
  public LinkedList<Integer> linkedListRemove(Tuple2<Integer, LinkedList<Integer>> v) {
    int size = v._1();
    LinkedList<Integer> list = v._2();

    Random random = new Random(size);
    Collections.shuffle(list, random);
    random = new Random(size);
    for (int i = 0; i < size; i++) {
      list.remove((Integer) random.nextInt());
    }

    return list;
  }
}
