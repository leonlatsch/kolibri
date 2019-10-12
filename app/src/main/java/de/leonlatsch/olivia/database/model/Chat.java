package de.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "chat")
public class Chat extends Model {

    @Column(name = "cid", index = true)
    private String cid;

    @Column(name = "first_member")
    private int firstMember;

    @Column(name = "second_member")
    private int secondMember;

    public Chat() {}

    public Chat(String cid, int firstMember, int secondMember) {
        this.cid = cid;
        this.firstMember = firstMember;
        this.secondMember = secondMember;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getFirstMember() {
        return firstMember;
    }

    public void setFirstMember(int firstMember) {
        this.firstMember = firstMember;
    }

    public int getSecondMember() {
        return secondMember;
    }

    public void setSecondMember(int secondMember) {
        this.secondMember = secondMember;
    }
}
