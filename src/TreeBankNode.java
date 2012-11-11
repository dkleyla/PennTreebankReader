/*-
 * PennTreebankReader TreeBankNode.java
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
import java.util.Iterator;
import java.util.LinkedList;

public class TreeBankNode implements Iterable<TreeBankNode> {

  protected String                 tag;
  protected String                 word;
  private TreeBankNode             parent;
  private LinkedList<TreeBankNode> children;

  TreeBankNode() {
    children = new LinkedList<TreeBankNode>();
  }

  public TreeBankNode getParent() {
    return parent;
  }

  public String getTag() {
    return tag;
  }

  public String getWord() {
    return word;
  }

  public boolean isLeaf() {
    return children.isEmpty();
  }

  public void add(TreeBankNode child) {
    children.add(child);
    child.parent = this;
  }

  private boolean hasNextSiblingNode() {
    if (parent == null) {
      return false;
    }
    return parent.children.indexOf(this) < parent.children.size() - 1;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (TreeBankNode p = parent; p != null; p = p.parent) {
      if (p.hasNextSiblingNode())
        sb.insert(0, "│ ");
      else
        sb.insert(0, "  ");
    }
    if (hasNextSiblingNode()) {
      sb.append("├ ");
    } else {
      sb.append("└ ");
    }
    if (isLeaf()) {
      sb.append(tag + " " + word);
    } else {
      sb.append(tag + "\n");
      for (int i = 0; i < children.size(); i++) {
        TreeBankNode c = children.get(i);
        if (i == children.size() - 1) {
          sb.append(c);
        } else {
          sb.append(c + "\n");
        }
      }
    }
    return sb.toString();
  }

  @Override
  public Iterator<TreeBankNode> iterator() {
    return children.iterator();
  }
}
