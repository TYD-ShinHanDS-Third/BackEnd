package com.shinhan.education.controller;

import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shinhan.education.naversms.Naver_Sens_Approved;
import com.shinhan.education.naversms.Naver_Sens_V2;

@RestController
@RequestMapping("/hows")
public class SmsController {
    private Naver_Sens_V2 naverSens;

    public SmsController() {
        this.naverSens = new Naver_Sens_V2();
    }
    
    //은행원이 승인 or 부접합 눌렀을때 안내 메세지 전송
    @PostMapping("/phone")
    public String apporveSms(@RequestParam("tel") String tel,
                             @RequestParam("membername") String membername,
                             @RequestParam("loanname") String loanname) {
        Naver_Sens_Approved sensApproved = new Naver_Sens_Approved();
        sensApproved.send_msg(tel, membername, loanname);
        
        return "success";
    }
    
    // 회원가입시 본인확인 메세지 전송
    @PostMapping("/send")
    public String sendSms(@RequestParam("tel")String tel) {
        String verificationCode = sendRandomMessage(tel);
        return verificationCode;
    }
    
    // 문자 발송
    private String sendRandomMessage(String tel) {
        String verificationCode = generateRandomNumber();
        naverSens.send_msg(tel, verificationCode);
        return verificationCode;
    }

    // 랜덤한 인증번호를 생성
    private String generateRandomNumber() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr.append(ran);
        }
        String verificationCode = numStr.toString();
        System.out.println("인증번호 생성: " + verificationCode);
        return verificationCode;
    }
}