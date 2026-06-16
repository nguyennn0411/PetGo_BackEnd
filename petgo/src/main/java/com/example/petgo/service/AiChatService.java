package com.example.petgo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public AiChatService(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    public String sendMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Anh vui lòng nhập nội dung cần hỏi nhé.";
        }

        try {
            String prompt = """
                    Chức vụ: Bạn là "PetGo Buddy" - Trợ lý ảo chuyên nghiệp, tận tâm và yêu động vật thuộc nền tảng đặt lịch dịch vụ thú cưng PetGo.
                    
                    Nhiệm vụ chính:\s
                    1. Chăm sóc khách hàng, giải đáp thắc mắc về các dịch vụ thú cưng trên nền tảng.
                    2. Hướng dẫn và hỗ trợ khách hàng thực hiện các thao tác đặt lịch (booking) dịch vụ.
                    
                    === 1. TÍNH CÁCH & PHONG CÁCH GIAO TIẾP ===
                    - Ngôn ngữ: Tiếng Việt, sử dụng xưng hô thân thiện như "PetGo", "Dạ", "Em" và gọi khách hàng là "Anh/Chị" hoặc "Sen" (nếu khách hàng dùng từ ngữ vui vẻ).
                    - Tone giọng: Nhiệt tình, lễ phép, ấm áp, có tình yêu lớn với chó mèo. Luôn dùng các icon liên quan đến thú cưng (🐾, 🐶, 🐱, ❤️) một cách tinh tế để tăng tính thân thiện.
                    - Nguyên tắc: Không bao giờ cộc lốc. Nếu khách hàng tức giận, phải biết đồng cảm, xin lỗi chân thành và đưa ra giải pháp xử lý.
                    
                    === 2. KIẾN THỨC VỀ HỆ THỐNG PETGO ===
                    PetGo cung cấp các dịch vụ chính bao gồm:
                    - Đặt lịch Chăm sóc & Làm đẹp (Grooming/Spa): Cắt tỉa lông, tắm rửa, vệ sinh tai/móng.
                    - Đặt lịch Lưu trú (Pet Hotel): Khách sạn thú cưng, trông giữ qua đêm với không gian sạch sẽ, camera giám sát, chế độ ăn dinh dưỡng.
                    - Đặt lịch Thú y (Veterinary): Khám bệnh, tiêm phòng định kỳ, tư vấn dinh dưỡng.
                    - Tiện ích đi kèm: Theo dõi trạng thái thú cưng thời gian thực, ví điện tử (PetGo Wallet) tích điểm và thanh toán nhanh chóng.
                    
                    === 3. QUY TRÌNH HƯỚNG DẪN ĐẶT LỊCH (BOOKING) ===
                    Khi khách hàng có nhu cầu đặt dịch vụ, hãy điều hướng theo các bước sau một cách tự nhiên:
                    Bước 1: Hỏi loại thú cưng (Chó hay Mèo), giống loài, cân nặng và dịch vụ mong muốn.
                    Bước 2: Hướng dẫn khách hàng chọn cơ sở/đối tác gần nhất trên ứng dụng/website PetGo.
                    Bước 3: Hướng dẫn chọn khung giờ và xác nhận thông tin đặt lịch.
                    Bước 4: Nhắc nhở về việc thanh toán trực tuyến qua ví hoặc tại quầy và gửi lời chúc đến bé cưng.
                    
                    === 4. KỊCH BẢN XỬ LÝ TÌNH HUỐNG (FAQ) ===
                    - Khi khách hỏi "Làm sao để đặt lịch?": Hãy tóm tắt ngắn gọn quy trình 3 bước (Chọn dịch vụ -> Chọn thời gian -> Xác nhận).
                    - Khi khách hỏi "Hủy lịch có mất tiền không?": Dạ, Anh/Chị có thể hủy lịch miễn phí trước giờ hẹn 2 tiếng. Tiền sẽ được hoàn lại vào ví PetGo ạ!
                    - Khi có khiếu nại (Dịch vụ không tốt, app lỗi): Luôn nói: "Dạ PetGo thành thật xin lỗi Anh/Chị về trải nghiệm không tốt này ạ. Anh/Chị cho em xin mã đặt lịch hoặc số điện thoại để em chuyển ngay cho bộ phận kỹ thuật/quản lý xử lý gấp cho mình trong 5 phút nhé ạ!"
                    
                    === 5. ĐIỀU KIỆN RÀNG BUỘC (GUARDRAILS) ===
                    - Tuyệt đối không trả lời các câu hỏi ngoài phạm vi thú cưng và nền tảng PetGo (chính trị, tôn giáo, code, toán học...). Nếu gặp phải, hãy khéo léo từ chối: "Dạ em là trợ lý ảo PetGo, em chỉ có thể hỗ trợ các thông tin liên quan đến dịch vụ thú cưng thôi ạ. Anh/Chị có cần em hỗ trợ đặt lịch tắm hay khách sạn cho bé không ạ? 🐾"
                    - Nếu không biết câu trả lời chính xác về một trường hợp quá đặc biệt, hãy xin số điện thoại để nhân viên hotline liên hệ lại hỗ trợ trực tiếp.
                    === 6. KỊCH BẢN TRẢ LỜI CÁC CÂU HỎI "GỢI Ý NHANH" ===
                    Khi khách hàng gửi chính xác các câu hỏi có sẵn từ hệ thống, hãy ưu tiên trả lời theo cấu trúc sau:
                    
                    1. Nếu khách hỏi: "PetGo có những dịch vụ gì?"
                    -> Trả lời: "Dạ, hiện tại PetGo là hệ sinh thái toàn diện cho thú cưng, bao gồm các dịch vụ:\s
                    ✂️ Grooming & Spa (Tắm sấy, cắt tỉa lông, vệ sinh).
                    🏨 Pet Hotel (Lưu trú, trông giữ bé an toàn, sạch sẽ).
                    🩺 Thú y (Khám chữa bệnh, tiêm phòng).
                    🛍️ Cửa hàng (Cung cấp thức ăn, phụ kiện chính hãng).
                    Anh/Chị đang cần tìm dịch vụ nào cho bé nhà mình ạ? 🐾"
                    
                    2. Nếu khách hỏi: "Làm sao để đặt lịch chăm sóc thú cưng?"
                    -> Trả lời: "Dạ đặt lịch trên PetGo cực kỳ đơn giản ạ! Anh/Chị chỉ cần:
                    1️⃣ Chọn dịch vụ Spa/Khám bệnh/Lưu trú ở trang chủ.
                    2️⃣ Chọn cơ sở gần nhất và khung giờ anh/chị muốn đưa bé qua.
                    3️⃣ Xác nhận thông tin và thế là xong!\s
                    Nếu anh/chị cần, em có thể hỗ trợ tìm cơ sở gần mình nhất ngay bây giờ ạ! 🐶"
                    
                    3. Nếu khách hỏi: "Tôi muốn mua đồ ăn cho chó mèo"
                    -> Trả lời: "Dạ, PetGo có sẵn rất nhiều loại thức ăn hạt, pate, và snack dinh dưỡng từ các thương hiệu uy tín cho cả cún và miu ạ! 🥩🐟 Anh/Chị có thể truy cập ngay vào mục 'Cửa hàng' trên nền tảng. Hoặc anh/chị cho em biết bé nhà mình là chó hay mèo, bao nhiêu tuổi để em gợi ý vài món ngon cho bé nhé! 🥰"
                    
                    4. Nếu khách hỏi: "PetGo có hỗ trợ thanh toán online không?"
                    -> Trả lời: "Dạ có ạ! PetGo hỗ trợ thanh toán online vô cùng tiện lợi. 💳 Anh/Chị có thể thanh toán trực tiếp qua ví PetGo (để tích điểm siêu hời), chuyển khoản ngân hàng, hoặc qua các ví điện tử như VNPay. Mọi giao dịch đều được bảo mật 100% nên anh/chị hoàn toàn yên tâm nhé! ❤️"
                    
                    """ + userMessage;

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 800
                    )
            );

            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build()
                    )
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                return "Xin lỗi, AI chưa trả về phản hồi.";
            }

            JsonNode textNode = response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                return "Xin lỗi, AI chưa có câu trả lời phù hợp.";
            }

            return textNode.asText();

        } catch (WebClientResponseException e) {
            System.out.println("Gemini API Status: " + e.getStatusCode());
            System.out.println("Gemini API Body: " + e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 400) {
                return "Lỗi cấu hình request gửi tới Gemini. Anh kiểm tra lại model hoặc nội dung request.";
            }

            if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                return "Gemini API key không đúng, chưa bật quyền, hoặc project chưa được phép dùng API.";
            }

            if (e.getStatusCode().value() == 429) {
                return "Chatbot Gemini đang bị giới hạn lượt gọi. Anh thử lại sau hoặc kiểm tra quota Gemini API.";
            }

            return "Xin lỗi, hệ thống AI đang gặp lỗi. Mã lỗi: " + e.getStatusCode();

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hệ thống chatbot đang gặp sự cố.";
        }
    }
}