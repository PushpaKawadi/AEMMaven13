package com.aem.community.core.services;

import java.io.InputStream;

/**
 * @author 105876
 *
 */
public interface AssetService {

	InputStream readCRXAsset(String assetPath);
}
