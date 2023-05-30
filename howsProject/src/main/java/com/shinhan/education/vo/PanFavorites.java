package com.shinhan.education.vo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PANFAVORITES")
public class PanFavorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int panid; // 모집공고번호
    @ManyToOne
    @JoinColumn(name = "memberid")
    private String member; // 회원아이디
    private String panname; // 모집 공고 이름
    private Date panstartdate; // 공고 시작일
    private Date panenddate; // 공고 종료일
}