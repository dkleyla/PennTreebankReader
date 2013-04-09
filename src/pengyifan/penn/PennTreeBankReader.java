package pengyifan.penn;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class PennTreeBankReader implements Closeable {

  InputStream in;
  int         currentChar;

  public PennTreeBankReader(InputStream in)
      throws IOException {
    this.in = in;
    currentChar = nextChar();
  }

  public PennTreeBankReader(File file)
      throws IOException {
    this(new FileInputStream(file));
  }

  public PennTreeBankReader(String filename)
      throws IOException {
    this(new FileInputStream(filename));
  }

  private int nextChar()
      throws IOException {
    return in.read();
  }

  private String nextToken()
      throws IOException {
    if (currentChar == -1) {
      return null;
    }
    if (currentChar == '(' || currentChar == ')') {
      String s = Character.toString((char) currentChar);
      currentChar = nextChar();
      return s;
    }

    // white space
    while (Character.isWhitespace(currentChar)) {
      currentChar = nextChar();
    }

    if (currentChar == -1) {
      return null;
    }
    if (currentChar == '(' || currentChar == ')') {
      String s = Character.toString((char) currentChar);
      currentChar = nextChar();
      return s;
    }
    StringBuilder sb = new StringBuilder();
    sb.append((char) currentChar);
    currentChar = nextChar();
    while (currentChar != '('
        && currentChar != ')'
        && currentChar != -1
        && !Character.isWhitespace(currentChar)) {
      sb.append((char) currentChar);
      currentChar = nextChar();
    }
    return sb.toString();

  }

  /**
   * Read a single ptb tree.
   * 
   * @return a ptb tree, or null if the end of the stream has been reached.
   * @throws IOException
   */
  public TreeModel readPtbTree()
      throws IOException {

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
    DefaultMutableTreeNode current = root;

    int state = 0;
    while (true) {
      String s = nextToken();
      switch (state) {
      case 0:
        if (s == null) {
          return null;
        } else if (s.equals("(")) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode();
          current.add(child);
          current = child;
          state = 1;
        } else {
          throw new IllegalArgumentException("the ptb should start with [(]");
        }
        break;
      case 1:
        if (s == null || s.equals("(") || s.equals(")")) {
          throw new IllegalArgumentException("expecting [tag]");
        } else {
          current.setUserObject(s);
          state = 2;
        }
        break;
      case 2:
        if (s == null || s.equals(")")) {
          throw new IllegalArgumentException("expecting [(] or [word]");
        } else if (s.equals("(")) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode();
          current.add(child);
          current = child;
          state = 1;
        } else {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode(s);
          current.add(child);
          state = 3;
        }
        break;
      case 3:
        if (s == null) {
          throw new IllegalArgumentException("expecting [(] or [)]");
        }
        if (s.equals(")")) {
          if (current == null) {
            throw new IllegalArgumentException("too much [)]");
          }
          current = (DefaultMutableTreeNode) current.getParent();
          if (current.getParent() == null) {
            return new DefaultTreeModel(root);
          }
        } else if (s.equals("(")) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode();
          current.add(child);
          current = child;
          state = 1;
        }
        break;
      }
    }
  }

  @Override
  public void close()
      throws IOException {
    in.close();
  }
}
