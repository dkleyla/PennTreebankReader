package pengyifan.penn;

import java.io.IOException;

import javax.swing.tree.TreeModel;

public class Test {

  public static void main(String args[])
      throws IOException {
    PennTreeBankReader reader = new PennTreeBankReader("test.ptb");
    TreeModel tree = null;
    while ((tree = reader.readPtbTree()) != null) {
      System.out.println(PtbString.ptbString(tree));
    }
    reader.close();
  }

}
