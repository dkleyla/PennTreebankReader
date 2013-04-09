package pengyifan.penn;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class PtbString {

  // │
  public static final String BAR    = bar(1);
  // └
  public static final String END    = bar(2);
  // ├
  public static final String MIDDLE = bar(3);

  private static String bar(int i) {
    try {
      switch (i) {
      case 1:
        return new String(new byte[] { -30, -108, -126 }, "utf8");
      case 2:
        return new String(new byte[] { -30, -108, -108 }, "utf8");
      case 3:
        return new String(new byte[] { -30, -108, -100 }, "utf8");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String ptbString(TreeModel tree) {
    StringBuffer sb = new StringBuffer();

    @SuppressWarnings("unchecked")
    Enumeration<DefaultMutableTreeNode> itr = ((DefaultMutableTreeNode) tree
        .getRoot()).preorderEnumeration();

    while (itr.hasMoreElements()) {
      DefaultMutableTreeNode tn = itr.nextElement();
      if (tn.isLeaf()) {
        continue;
      }
      // add prefix
      for (TreeNode p : tn.getPath()) {
        // if parent has sibling node
        if (p == tn) {
          ;
        } else if (hasNextSibling((DefaultMutableTreeNode) p)) {
          sb.append(BAR + " ");
        } else {
          sb.append("  ");
        }
      }
      // if root has sibling node
      if (hasNextSibling(tn)) {
        sb.append(MIDDLE + " ");
      } else {
        sb.append(END + " ");
      }
      if (tn.isRoot()) {
        sb.append("\n");
      } else if (tn.getChildCount() == 1) {
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) tn
            .getChildAt(0);
        if (child.isLeaf()) {
          sb.append(tn.getUserObject() + " " + child.getUserObject() + "\n");
        } else {
          sb.append(tn.getUserObject() + "\n");
        }
      } else {
        sb.append(tn.getUserObject() + "\n");
      }

    }

    return sb.toString();
  }

  private static boolean hasNextSibling(DefaultMutableTreeNode tn) {
    return tn.getNextSibling() != null;
  }
}
