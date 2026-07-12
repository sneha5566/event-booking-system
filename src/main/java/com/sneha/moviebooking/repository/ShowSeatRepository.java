package com.sneha.moviebooking.repository;

import com.sneha.moviebooking.entity.Show;
import com.sneha.moviebooking.entity.ShowSeat;
import com.sneha.moviebooking.entity.ShowSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShow(Show show);

    List<ShowSeat> findByShowAndStatus(
            Show show,
            ShowSeatStatus status
    );
    boolean existsByShowIdAndSeatId(Long showId, Long seatId);
    List<ShowSeat> findByIdIn(List<Long> ids);

    List<ShowSeat> findByShowAndIdIn(Show show, List<Long> ids);
}
