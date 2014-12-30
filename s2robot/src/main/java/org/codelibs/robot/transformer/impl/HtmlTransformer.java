/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.robot.transformer.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.xpath.CachedXPathAPI;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.robot.Constants;
import org.codelibs.robot.RobotCrawlAccessException;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.container.RobotContainer;
import org.codelibs.robot.entity.AccessResultData;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.ResultData;
import org.codelibs.robot.helper.EncodingHelper;
import org.codelibs.robot.helper.UrlConvertHelper;
import org.codelibs.robot.util.CharUtil;
import org.codelibs.robot.util.ResponseDataUtil;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * HtmlTransformer stores WEB data as HTML content.
 *
 * @author shinsuke
 *
 */
public class HtmlTransformer extends AbstractTransformer {

    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(HtmlTransformer.class);

    protected static final String LOCATION_HEADER = "Location";

    @Resource
    protected RobotContainer robotContainer;

    protected Map<String, String> featureMap = new HashMap<String, String>();

    protected Map<String, String> propertyMap = new HashMap<String, String>();

    protected Map<String, String> childUrlRuleMap = new LinkedHashMap<String, String>();

    protected String defaultEncoding;

    // If you want to follow a html spec, use 512.
    protected int preloadSizeForCharset = 2048;

    protected Pattern invalidUrlPattern = Pattern.compile("^\\s*javascript:|"
            + "^\\s*mailto:|" + "^\\s*irc:|" + "^\\s*skype:|" + "^\\s*about:|"
            + "^\\s*fscommand:|" + "^\\s*aim:|" + "^\\s*msnim:|"
            + "^\\s*news:|" + "^\\s*tel:|" + "^\\s*unsaved:|" + "^\\s*callto:",
            Pattern.CASE_INSENSITIVE);

    private final ThreadLocal<CachedXPathAPI> xpathAPI = new ThreadLocal<CachedXPathAPI>();

    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotCrawlAccessException("No response body.");
        }

        final File tempFile = ResponseDataUtil
                .createResponseBodyFile(responseData);

        FileInputStream fis = null;

        // encoding
        try {
            fis = new FileInputStream(tempFile);
            responseData.setResponseBody(fis);
            updateCharset(responseData);
        } catch (final RobotSystemException e) {
            IOUtils.closeQuietly(fis);
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw e;
        } catch (final Exception e) {
            IOUtils.closeQuietly(fis);
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw new RobotSystemException("Could not load response data: "
                    + responseData.getUrl(), e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());

        // data
        try {
            fis = new FileInputStream(tempFile);
            responseData.setResponseBody(fis);
            storeData(responseData, resultData);
        } catch (final RobotSystemException e) {
            IOUtils.closeQuietly(fis);
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw e;
        } catch (final Exception e) {
            IOUtils.closeQuietly(fis);
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw new RobotSystemException("Could not store data.", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        if (isHtml(responseData)) {
            // urls
            try {
                fis = new FileInputStream(tempFile);
                responseData.setResponseBody(fis);
                storeChildUrls(responseData, resultData);
            } catch (final RobotSystemException e) {
                IOUtils.closeQuietly(fis);
                // clean up
                if (!tempFile.delete()) {
                    logger.warn("Could not delete a temp file: " + tempFile);
                }
                throw e;
            } catch (final Exception e) {
                IOUtils.closeQuietly(fis);
                // clean up
                if (!tempFile.delete()) {
                    logger.warn("Could not delete a temp file: " + tempFile);
                }
                throw new RobotSystemException("Could not store data.", e);
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }

        final Object redirectUrlObj = responseData.getMetaDataMap().get(
                LOCATION_HEADER);
        if (redirectUrlObj instanceof String) {
            final UrlConvertHelper urlConvertHelper = robotContainer
                    .getComponent("urlConvertHelper");
            resultData.addUrl(RequestDataBuilder.newRequestData().get()
                    .url(urlConvertHelper.convert(redirectUrlObj.toString()))
                    .build());
        }

        // clean up
        if (!tempFile.delete()) {
            logger.warn("Could not delete a temp file: " + tempFile);
        }

        return resultData;
    }

    protected boolean isHtml(final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
        if ("text/html".equals(mimeType)
                || "application/xhtml+xml".equals(mimeType)) {
            return true;
        }
        return false;
    }

    public void addChildUrlRule(final String tagName, final String attrName) {
        if (StringUtil.isNotBlank(tagName) && StringUtil.isNotBlank(attrName)) {
            childUrlRuleMap.put(tagName, attrName);
        }
    }

    protected CachedXPathAPI getXPathAPI() {
        CachedXPathAPI cachedXPathAPI = xpathAPI.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new CachedXPathAPI();
            xpathAPI.set(cachedXPathAPI);
        }
        return cachedXPathAPI;
    }

    protected void storeChildUrls(final ResponseData responseData,
            final ResultData resultData) {
        List<RequestData> requestDataList = new ArrayList<RequestData>();
        try {
            final DOMParser parser = getDomParser();
            parser.parse(new InputSource(responseData.getResponseBody()));
            final Document document = parser.getDocument();
            // base href
            final String baseHref = getBaseHref(document);
            final URL url = new URL(baseHref == null ? responseData.getUrl()
                    : baseHref);
            for (final Map.Entry<String, String> entry : childUrlRuleMap
                    .entrySet()) {
                for (final String childUrl : getUrlFromTagAttribute(url,
                        document, entry.getKey(), entry.getValue(),
                        responseData.getCharSet())) {
                    requestDataList.add(RequestDataBuilder.newRequestData()
                            .get().url(childUrl).build());
                }
            }
            requestDataList = convertChildUrlList(requestDataList);
        } catch (final Exception e) {
            logger.warn("Could not create child urls.", e);
        } finally {
            xpathAPI.remove();
        }
        resultData.addAllUrl(requestDataList);

        resultData.addAllUrl(responseData.getChildUrlSet());

        final RequestData requestData = responseData.getRequestData();
        resultData.removeUrl(requestData);
        resultData.removeUrl(getDuplicateUrl(requestData));
    }

    protected List<RequestData> convertChildUrlList(
            final List<RequestData> requestDataList) {
        try {
            final UrlConvertHelper urlConvertHelper = robotContainer
                    .getComponent("urlConvertHelper");
            for (final RequestData requestData : requestDataList) {
                requestData.setUrl(urlConvertHelper.convert(requestData
                        .getUrl()));
            }
            return requestDataList;
        } catch (final Exception e) {
            // NOP
        }
        return requestDataList;
    }

    protected void storeData(final ResponseData responseData,
            final ResultData resultData) {
        final byte[] data = InputStreamUtil.getBytes(responseData
                .getResponseBody());
        resultData.setData(data);
        resultData.setEncoding(responseData.getCharSet());
    }

    protected void updateCharset(final ResponseData responseData) {
        final String encoding = loadCharset(responseData.getResponseBody());
        if (encoding == null) {
            if (defaultEncoding == null) {
                responseData.setCharSet(Constants.UTF_8);
            } else if (responseData.getCharSet() == null) {
                responseData.setCharSet(defaultEncoding);
            }
        } else {
            responseData.setCharSet(encoding.trim());
        }

        if (!isSupportedCharset(responseData.getCharSet())) {
            responseData.setCharSet(Constants.UTF_8);
        }
    }

    protected boolean isSupportedCharset(final String charsetName) {
        if (charsetName == null) {
            return false;
        }
        try {
            Charset.forName(charsetName);
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    protected String loadCharset(final InputStream inputStream) {
        BufferedInputStream bis = null;
        String encoding = null;
        try {
            bis = new BufferedInputStream(inputStream);
            final byte[] buffer = new byte[preloadSizeForCharset];
            final int size = bis.read(buffer);
            if (size != -1) {
                final String content = new String(buffer, 0, size);
                encoding = parseCharset(content);
            }
        } catch (final IOException e) {
            throw new RobotCrawlAccessException("Could not load a content.", e);
        }

        try {
            final EncodingHelper encodingHelper = robotContainer
                    .getComponent("encodingHelper");
            encoding = encodingHelper.normalize(encoding);
        } catch (final Exception e) {
            // NOP
        }

        return encoding;
    }

    protected String parseCharset(final String content) {
        final Pattern pattern = Pattern.compile(
                "; *charset *= *([a-zA-Z0-9\\-_]+)", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    protected RequestData getDuplicateUrl(final RequestData requestData) {
        final String url = requestData.getUrl();
        if (url.endsWith("/")) {
            requestData.setUrl(url.substring(0, url.length() - 1));
        } else {
            requestData.setUrl(url + "/");
        }
        return requestData;
    }

    protected DOMParser getDomParser() {
        final DOMParser parser = new DOMParser();
        try {
            // feature
            for (final Map.Entry<String, String> entry : featureMap.entrySet()) {
                parser.setFeature(entry.getKey(), "true".equalsIgnoreCase(entry
                        .getValue()) ? true : false);
            }

            // property
            for (final Map.Entry<String, String> entry : propertyMap.entrySet()) {
                parser.setProperty(entry.getKey(), entry.getValue());
            }
        } catch (final Exception e) {
            throw new RobotSystemException("Invalid parser configuration.", e);
        }

        return parser;
    }

    protected String getBaseHref(final Document document) {
        NodeList list;
        try {
            list = getXPathAPI().selectNodeList(document, "//BASE");
        } catch (final Exception e) {
            logger.warn("Could not get a base tag. ", e);
            return null;
        }
        if (list.getLength() > 0) {
            final Element element = (Element) list.item(0);
            String attrValue = element.getAttribute("href");
            if (StringUtil.isNotBlank(attrValue)) {
                // if starting with www, append a protocol
                if (attrValue.startsWith("www.")) {
                    attrValue = "http://" + attrValue;
                }
                return attrValue;
            }
        }
        return null;
    }

    protected List<String> getUrlFromTagAttribute(final URL url,
            final Document document, final String xpath, final String attr,
            final String encoding) {
        if (logger.isDebugEnabled()) {
            logger.debug("Base URL: " + url);
        }
        final List<String> urlList = new ArrayList<String>();
        try {
            final NodeList list = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < list.getLength(); i++) {
                final Element element = (Element) list.item(i);
                final String attrValue = element.getAttribute(attr);
                if (isValidPath(attrValue)) {
                    addChildUrlFromTagAttribute(urlList, url, attrValue,
                            encoding);
                }
            }
        } catch (final TransformerException e) {
            logger.warn("Could not get urls: (" + xpath + ", " + attr + ")", e);
        }
        return urlList;
    }

    protected void addChildUrlFromTagAttribute(final List<String> urlList,
            final URL url, final String attrValue, final String encoding) {
        try {
            final String childUrlValue = attrValue.trim();
            final URL childUrl = childUrlValue.startsWith("?") ? new URL(
                    url.toExternalForm() + childUrlValue) : new URL(url,
                    childUrlValue);
            final String u = encodeUrl(normalizeUrl(childUrl.toExternalForm()),
                    encoding);
            if (logger.isDebugEnabled()) {
                logger.debug(attrValue + " -> " + u);
            }
            if (StringUtil.isNotBlank(u)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Add Child: " + u);
                }
                urlList.add(u);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skip Child: " + u);
                }
            }
        } catch (final MalformedURLException e) {
            logger.warn("Malformed URL: " + attrValue, e);
        }
    }

    protected String encodeUrl(final String url, final String enc) {
        if (StringUtil.isBlank(url) || StringUtil.isBlank(enc)) {
            return url;
        }

        final StringBuilder buf = new StringBuilder(url.length() + 100);
        for (final char c : url.toCharArray()) {
            if (CharUtil.isUrlChar(c)) {
                buf.append(c);
            } else {
                try {
                    buf.append(URLEncoder.encode(String.valueOf(c), enc));
                } catch (final UnsupportedEncodingException e) {
                    // NOP
                }
            }
        }
        return buf.toString();
    }

    protected String normalizeUrl(final String u) {
        if (u == null) {
            return null;
        }

        // trim
        String url = u.trim();

        int idx = url.indexOf('#');
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        url = url.replaceAll(Pattern.quote("/./"), "/");

        idx = url.indexOf(";jsessionid");
        if (idx >= 0) {
            url = url.replaceFirst(";jsessionid=[a-zA-Z0-9\\.]*", "");
        }

        if (url.indexOf(' ') >= 0) {
            // invalid URL
            if (logger.isDebugEnabled()) {
                logger.debug("INVALID URL: " + url);
            }
            return null;
        }

        String oldUrl = null;
        while (url.indexOf("/../") >= 0 && !url.equals(oldUrl)) {
            oldUrl = url;
            url = url.replaceFirst("/[^/]+/\\.\\./", "/");
        }

        url = url.replaceAll("([^:])/+", "$1/");

        return url;
    }

    protected boolean isValidPath(final String path) {
        if (StringUtil.isBlank(path)) {
            return false;
        }

        final Matcher matcher = invalidUrlPattern.matcher(path);
        if (matcher.find()) {
            return false;
        }

        return true;
    }

    public void addFeature(final String key, final String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new RobotSystemException("key or value is null.");
        }

        featureMap.put(key, value);
    }

    public void addProperty(final String key, final String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new RobotSystemException("key or value is null.");
        }

        propertyMap.put(key, value);
    }

    /**
     * Returns data as HTML content of String.
     *
     */
    @Override
    public Object getData(final AccessResultData accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new RobotSystemException("Transformer is invalid. Use "
                    + accessResultData.getTransformerName()
                    + ". This transformer is " + getName() + ".");
        }

        final byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        final String encoding = accessResultData.getEncoding();
        try {
            return new String(data, encoding == null ? Constants.UTF_8
                    : encoding);
        } catch (final UnsupportedEncodingException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalid charsetName: " + encoding
                        + ". Changed to " + Constants.UTF_8, e);
            }
            return new String(data, Constants.UTF_8_CHARSET);
        }
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(final Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(final Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public Map<String, String> getChildUrlRuleMap() {
        return childUrlRuleMap;
    }

    public void setChildUrlRuleMap(final Map<String, String> childUrlRuleMap) {
        this.childUrlRuleMap = childUrlRuleMap;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public int getPreloadSizeForCharset() {
        return preloadSizeForCharset;
    }

    public void setPreloadSizeForCharset(final int preloadSizeForCharset) {
        this.preloadSizeForCharset = preloadSizeForCharset;
    }

    public Pattern getInvalidUrlPattern() {
        return invalidUrlPattern;
    }

    public void setInvalidUrlPattern(final Pattern invalidUrlPattern) {
        this.invalidUrlPattern = invalidUrlPattern;
    }
}