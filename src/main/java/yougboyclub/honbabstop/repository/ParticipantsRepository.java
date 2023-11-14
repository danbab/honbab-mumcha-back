package yougboyclub.honbabstop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yougboyclub.honbabstop.domain.Board;
import yougboyclub.honbabstop.domain.Participants;
import yougboyclub.honbabstop.domain.User;

import java.util.List;

public interface ParticipantsRepository extends JpaRepository <Participants, Long> {

    //내가 참여한 게시들 가져오기
//    @Query("select p.board from participants p where p.user = :user and p.board.writer != :user")

//    @Query("select p.board from Participants p where p.user = :user")
//    List<Board> findByUser(User user);

    // 참여한 글 중에서 내가 쓴 글이 아닌 것만 가져오기
    @Query("SELECT p.board FROM Participants p WHERE p.user = :user AND p.board.writer != :user")  //@Query("select p.board from Participants p where p.user = :user") // and p.board.writer != :user"
    List<Board> findByUserNonWriter(@Param("user") User user);

    @Query("select p.board from Participants p where p.user =:user")
    List<Board> findByUser(@Param("user")User user);
}
