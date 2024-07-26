package com.oocourse.library2;

import java.util.Objects;

public class LibraryBookId {
    public LibraryBookId(Type type, String uid) {
        this.type = type;
        this.uid = uid;
    }

    /**
     * 解析给定字符串并构造 BookId
     *
     * @param fromString 格式为 "&lt;类别号&gt;-&lt;序列号&gt;"
     * @return BookId
     * @throws RuntimeException 若字符串无法解析
     */
    public static LibraryBookId parse(String fromString) {
        String[] parts = fromString.split("-");
        try {
            return new LibraryBookId(Type.valueOf(parts[0]), parts[1]);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid book id: " + fromString);
        }
    }

    public enum Type { A, B, C, AU, BU, CU }

    private final Type type;
    private final String uid;

    /**
     * 获取书籍类别
     *
     * @return 书籍类别
     */
    public Type getType() {
        return type;
    }

    /**
     * 获取书籍序列号
     *
     * @return 书籍序列号
     */
    public String getUid() {
        return uid;
    }

    /**
     * 判断书籍类别是否为 A
     *
     * @return 书籍类别是否为 A
     */
    public boolean isTypeA() {
        return type == Type.A;
    }

    /**
     * 判断书籍类别是否为 AU
     *
     * @return 书籍类别是否为 AU
     */
    public boolean isTypeAU() {
        return type == Type.AU;
    }

    /**
     * 判断书籍类别是否为 B
     *
     * @return 书籍类别是否为 B
     */
    public boolean isTypeB() {
        return type == Type.B;
    }

    /**
     * 判断书籍类别是否为 BU
     *
     * @return 书籍类别是否为 BU
     */
    public boolean isTypeBU() {
        return type == Type.BU;
    }

    /**
     * 判断书籍类别是否为 C
     *
     * @return 书籍类别是否为 C
     */
    public boolean isTypeC() {
        return type == Type.C;
    }

    /**
     * 判断书籍类别是否为 CU
     *
     * @return 书籍类别是否为 CU
     */
    public boolean isTypeCU() {
        return type == Type.CU;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LibraryBookId bookId = (LibraryBookId) o;
        return type == bookId.type && Objects.equals(uid, bookId.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, uid);
    }

    @Override
    public String toString() {
        return type + "-" + uid;
    }
}
