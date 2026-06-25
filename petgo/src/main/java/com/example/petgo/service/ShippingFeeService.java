package com.example.petgo.service;

import com.example.petgo.dto.ShippingFeeRequest;
import com.example.petgo.dto.ShippingFeeResponse;

public interface ShippingFeeService {

    ShippingFeeResponse calculateShippingFee(ShippingFeeRequest request);
}
