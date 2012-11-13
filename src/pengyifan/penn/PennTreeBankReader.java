package pengyifan.penn;
/*-
 * PennTreebankReader PennTreeBankReader.java
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
 ******************************************************************************/
import java.io.IOException;
import java.io.InputStream;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class PennTreeBankReader {

  InputStream in;
  TreeModel   next;
  int         currentChar;

  public static class Node {

    private final String tag;
    private final String word;

    public Node(String tag) {
      this(tag, null);
    }

    public Node(String tag, String word) {
      this.tag = tag;
      this.word = word;
    }

    public String getTag() {
      return tag;
    }

    public String getWord() {
      return word;
    }

    @Override
    public String toString() {
      return tag + " " + word;
    }
  }

  public PennTreeBankReader(InputStream in)
      throws IOException {
    this.in = in;
    currentChar = nextChar();
    next();
  }

  public boolean hasNext() {
    return next != null;
  }

  public TreeModel next()
      throws IOException {
    TreeModel curr = next;
    next = readNext();
    return curr;
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

  private TreeModel readNext()
      throws IOException {

    DefaultMutableTreeNode parent = new DefaultMutableTreeNode(new Node("ROOT"));

    int state = 0;
    while (true) {
      String s = nextToken();
      switch (state) {
      case 0:
        if (s == null) {
          return null;
        } else if (s.equals("(")) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode();
          parent.add(child);
          parent = child;
          state = 1;
        } else {
          throw new IllegalArgumentException("the ptb should start with [(]");
        }
        break;
      case 1:
        if (s == null || s.equals("(") || s.equals(")")) {
          throw new IllegalArgumentException("expecting [tag]");
        } else {
          parent.setUserObject(new Node(s));
          state = 2;
        }
        break;
      case 2:
        if (s == null || s.equals(")")) {
          throw new IllegalArgumentException("expecting [(] or [word]");
        } else if (s.equals("(")) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode();
          parent.add(child);
          parent = child;
          state = 1;
        } else {
          Node obj = (Node) parent.getUserObject();
          parent.setUserObject(new Node(obj.getTag(), s));
          state = 3;
        }
        break;
      case 3:
        if (s == null) {
          throw new IllegalArgumentException("expecting [(] or [)]");
        }
        if (s.equals(")")) {
          if (parent == null) {
            throw new IllegalArgumentException("too much [)]");
          }
          parent = (DefaultMutableTreeNode) parent.getParent();
          if (parent.getParent() == null) {
            return new DefaultTreeModel(parent);
          }
        } else if (s.equals("(")) {
          DefaultMutableTreeNode child = new DefaultMutableTreeNode();
          parent.add(child);
          parent = child;
          state = 1;
        }
        break;
      }
    }
  }
}
