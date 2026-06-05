package com.example.petgo.config;

import com.example.petgo.entity.User;
import com.example.petgo.entity.Wallet;
import com.example.petgo.entity.WalletSetting;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.WalletRepository;
import com.example.petgo.repository.WalletSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WalletDataInitializer implements CommandLineRunner {
    private static final String AUTO_CONFIRM_TOP_UP_KEY = "WALLET_AUTO_CONFIRM_TOP_UP";

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletSettingRepository walletSettingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        walletSettingRepository.findBySettingKey(AUTO_CONFIRM_TOP_UP_KEY).orElseGet(() -> {
            WalletSetting setting = new WalletSetting();
            setting.setSettingKey(AUTO_CONFIRM_TOP_UP_KEY);
            setting.setSettingValue("false");
            return walletSettingRepository.save(setting);
        });

        for (User user : userRepository.findAll()) {
            walletRepository.findByUserId(user.getId()).orElseGet(() -> {
                Wallet wallet = new Wallet();
                wallet.setUser(user);
                return walletRepository.save(wallet);
            });
        }
    }
}