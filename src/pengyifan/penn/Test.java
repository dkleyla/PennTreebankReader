package pengyifan.penn;
/*-
 * PennTreebankReader Test.java
 * 
 * Copyright (c) 2012, Yifan Peng 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Yifan Peng  BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class Test {

  public static void main(String args[])
      throws IOException {
    PennTreeBankReader reader = new PennTreeBankReader(new FileInputStream(
        "test.ptb"));
    while (reader.hasNext()) {
      TreeModel tree = reader.next();
      System.out.println(walk(tree));
    }
  }

  private static String walk(TreeModel tree) {
    StringBuffer sb = new StringBuffer();

    @SuppressWarnings("unchecked")
    Enumeration<DefaultMutableTreeNode> itr = ((DefaultMutableTreeNode) tree
        .getRoot()).preorderEnumeration();

    while (itr.hasMoreElements()) {
      DefaultMutableTreeNode tn = itr.nextElement();
      // add prefix
      for (TreeNode p : tn.getPath()) {
        // if parent has sibling node
        if (p == tn) {
          ;
        } else if (hasNextSibling((DefaultMutableTreeNode) p)) {
          sb.append("│ ");
        } else {
          sb.append("  ");
        }
      }
      // if root has sibling node
      if (hasNextSibling(tn)) {
        sb.append("├ ");
      } else {
        sb.append("└ ");
      }
      PennTreeBankReader.Node data = (PennTreeBankReader.Node) tn
          .getUserObject();
      if (tn.isRoot()) {
        sb.append("\n");
      } else if (tn.isLeaf()) {
        sb.append(data.getTag() + " " + data.getWord() + "\n");
      } else {
        sb.append(data.getTag() + "\n");
      }

    }

    return sb.toString();
  }

  private static boolean hasNextSibling(DefaultMutableTreeNode tn) {
    return tn.getNextSibling() != null;
  }
}
