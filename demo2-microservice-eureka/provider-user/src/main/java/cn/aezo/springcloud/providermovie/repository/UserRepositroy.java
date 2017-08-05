package cn.aezo.springcloud.providermovie.repository;

import cn.aezo.springcloud.providermovie.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by smalle on 2017/7/1.
 */
@Repository
public interface UserRepositroy extends JpaRepository<User, Long> {
}
