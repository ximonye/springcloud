package cn.aezo.springcloud.springbootrabbitmq.entity;

import java.math.BigDecimal;

/**
 * Created by smalle on 2017/7/1.
 */
public class User {
    private Long id;

    private String username;

    private Short age;

    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

