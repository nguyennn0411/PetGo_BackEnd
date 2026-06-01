package com.example.petgo.service;

import com.example.petgo.entity.Invoice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayOSPaymentService {
	@Value("${payos.api-url:https://api.payos.example}")
	private String payosApiUrl;

	@Value("${payos.client-id:}")
	private String clientId;

	@Value("${payos.api-key:}")
	private String apiKey;

	@Value("${payos.checksum-key:}")
	private String checksumKey;

	@Value("${payos.return-url:http://localhost:5173/checkout/success}")
	private String returnUrl;

	@Value("${payos.notify-url:http://localhost:8080/api/v1/payments/payos/webhook}")
	private String notifyUrl;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, String> createPaymentForInvoice(Invoice invoice) throws Exception {
		RestTemplate rt = new RestTemplate();
		String url = payosApiUrl + "/v1/checkout/create"; // adjust to real endpoint

		Map<String, Object> payload = new HashMap<>();
		payload.put("merchant_order_id", invoice.getInvoiceNumber());
		payload.put("amount", invoice.getTotalAmount());
		payload.put("currency", invoice.getCurrencyCode());
		payload.put("description", "Thanh toán hóa đơn " + invoice.getInvoiceNumber());
		payload.put("return_url", returnUrl);
		payload.put("notify_url", notifyUrl);

		String body = objectMapper.writeValueAsString(payload);
		String signature = hmacSha256(checksumKey, body);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (apiKey != null && !apiKey.isEmpty()) headers.set("X-API-KEY", apiKey);
		headers.set("X-SIGNATURE", signature);

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> resp = rt.postForEntity(url, request, String.class);

		// parse response (assume JSON contains checkout_url and transaction_id)
		Map<String, Object> respMap = objectMapper.readValue(resp.getBody(), Map.class);
		String checkoutUrl = respMap.getOrDefault("checkout_url", "").toString();
		String txnId = respMap.getOrDefault("transaction_id", "").toString();

		Map<String, String> result = new HashMap<>();
		result.put("checkoutUrl", checkoutUrl);
		result.put("transactionId", txnId);
		return result;
	}

	public boolean verifySignature(String payload, String signatureHeader) {
		try {
			String computed = hmacSha256(checksumKey, payload);
			return computed.equals(signatureHeader);
		} catch (Exception e) {
			return false;
		}
	}

	private String hmacSha256(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		byte[] macData = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		for (byte b : macData) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

}
