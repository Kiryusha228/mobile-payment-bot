package org.example.client;

import lombok.RequiredArgsConstructor;

import org.example.config.WebClientConfig;
import org.example.model.dto.ChangeMainPhoneDto;
import org.example.model.dto.CreatePhoneDto;
import org.example.model.dto.CreateUserDto;
import org.example.model.dto.PhoneDto;
import org.example.model.entity.PhoneEntity;
import org.example.model.entity.UserEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CrudClient {
    private final WebClientConfig webClient;

    public UserEntity createUser(CreateUserDto createUserDto) {
        return webClient.getWebClient()
                .post()
                .uri("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .retrieve()
                .bodyToMono(UserEntity.class)
                .block();
    }

    public PhoneEntity createPhone(CreatePhoneDto createPhoneDto) {
        return webClient.getWebClient()
                .post()
                .uri("/phone/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createPhoneDto)
                .retrieve()
                .bodyToMono(PhoneEntity.class)
                .block();
    }

    public void changeMainPhone(ChangeMainPhoneDto changeMainPhoneDto) {
        webClient.getWebClient()
                .patch()
                .uri("/phone/main/change")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(changeMainPhoneDto)
                .retrieve()
                .bodyToMono(void.class)
                .block();
    }

    public PhoneDto getMainPhoneByChatId(Long chatId) {
        return webClient.getWebClient()
                .get()
                .uri("/phone/get/main/{chatId}", chatId)
                .retrieve()
                .bodyToMono(PhoneDto.class)
                .block();
    }

    public Long getUserIdByChatId(Long chatId) {
        return webClient.getWebClient()
                .get()
                .uri("/user/get/id/{chatId}", chatId)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public List<PhoneDto> getPhonesByChatId(Long chatId) {
        return webClient.getWebClient()
                .get()
                .uri("phone/get/phones/{chatId}", chatId)
                .retrieve()
                .bodyToFlux(PhoneDto.class)
                .collectList()
                .block();
    }

    public PhoneDto getPhoneById(Long phoneId) {
        return webClient.getWebClient()
                .get()
                .uri("phone/get/{phoneId}", phoneId)
                .retrieve()
                .bodyToMono(PhoneDto.class)
                .block();
    }







}
