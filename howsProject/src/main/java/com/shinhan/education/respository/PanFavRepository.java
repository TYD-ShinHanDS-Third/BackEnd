package com.shinhan.education.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.vo.PanFavorites;
import com.shinhan.education.vo.PanFavoritesId;

public interface PanFavRepository extends JpaRepository<PanFavorites, PanFavoritesId> {
	Optional<PanFavorites> findById(PanFavoritesId id);
	void deleteById(PanFavoritesId id);
}
