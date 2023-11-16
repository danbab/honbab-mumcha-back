package yougboyclub.honbabstop.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yougboyclub.honbabstop.domain.Board;
import yougboyclub.honbabstop.domain.User;
import yougboyclub.honbabstop.dto.RequestBoardDto;
import yougboyclub.honbabstop.dto.ResponseBoardDto;
import yougboyclub.honbabstop.dto.UpdateBoardRequest;
import yougboyclub.honbabstop.repository.BoardRepository;
import yougboyclub.honbabstop.repository.ParticipantsRepository;
import yougboyclub.honbabstop.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

  private final BoardRepository boardRepository;

  private final ParticipantsRepository participantsRepository;

  private final UserRepository userRepository;

  //모집글 작성
  @Override
  public Board createBoard(RequestBoardDto requestBoardDto) {
    Optional<User> optionalUser = userRepository.findByEmail(requestBoardDto.getWriter().getEmail());
    if (optionalUser.isPresent()) {
      User getUser = optionalUser.get();
      System.out.println("이건 겟유저다 이 짜식아:: " + getUser);
      Board board = requestBoardDto.toEntity();
      board.setHit(0L);
      board.setStatus(0);
      board.setWriter(getUser);
      System.out.println("이건 세팅 다 끝난 보드~:: " + board);
      return boardRepository.save(board);
    }
    return null;
  }

  //모든 모집글 조회
  @Override
  public List<Board> findAllBoard() {
    return boardRepository.findAll();
  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  public List<Board> findByFoodCategory(String foodCategory) {
    return boardRepository.findByFoodCategory(foodCategory);
  }

  @Override
  public List<Board> findByPlaceCategory(String placeCategory) {
    return boardRepository.findByPlaceCategory(placeCategory);
  }

  @Override
  public List<Board> findByWriter(User user) {
    return boardRepository.findByWriter(user);
  }

  @Override
  @Transactional
  public Board update(Long id, UpdateBoardRequest request) {
    Board board = boardRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    board.update(request.getTitle(), request.getContent(), request.getTime(), request.getMeetDate(), request.getFoodCategory(), request.getPlaceCategory(), request.getPeople(), request.getRestaurantName(), request.getRestaurantAddress(), request.getLocationX(), request.getLocationY());
    return board;
  }

  @Override
  public List<Board> findByKeyword(String keyword) {
    return boardRepository.findByKeyword(keyword);
  }

  //모집글 상세조회(모집글 번호)
  public Board findById(Long id, User currentUser) {
    Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾지 못했습니다: " + id));

    // 본인 게시글이 아닐 경우에만 조회수 증가
    if (!board.getWriter().getId().equals(currentUser.getId())) {
      board.increaseHit();
    }
    return board;
  }

  //특정 모집글 수정
  @Override
  @Transactional
  public Board updateById(Long id, UpdateBoardRequest request) {
    Board board = boardRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("찾지 못했습니다. : " + id));
    board.update(request.getTitle(),
        request.getContent(),
        request.getTime(),
        request.getMeetDate(),
        request.getFoodCategory(),
        request.getPlaceCategory(),
        request.getPeople(),
        request.getRestaurantName(),
        request.getRestaurantAddress(),
        request.getLocationX(),
        request.getLocationY());
    return board;
  }

  //특정 모집글 삭제
  @Override
  @Transactional
  public void deleteById(Long id) {
    boardRepository.deleteById(id);
  }


  @Override
  public List<Board> findByUserNonWriter(User user) {

    return participantsRepository.findByUserNonWriter(user);
  }

  @Override
  public List<Board> findByUser(User user) {
    return participantsRepository.findByUser(user);
  }
}