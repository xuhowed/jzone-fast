package com.jzone.module.system.login.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.jzone.common.constant.CacheKeyConstants;
import com.jzone.framework.cache.CacheService;
import com.jzone.framework.exception.BusinessException;
import com.jzone.framework.web.JZoneSecurityProperties;
import com.jzone.module.system.login.pojo.vo.CaptchaVO;
import com.jzone.module.system.login.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final JZoneSecurityProperties securityProperties;
    private final CacheService cacheService;

    private static final int IMG_WIDTH = 320;
    private static final int IMG_HEIGHT = 160;
    private static final int PIECE_SIZE = 44;
    private static final int TOLERANCE = 6;
    private static final Duration CAPTCHA_TTL = Duration.ofMinutes(2);

    private static final Color[][] COLOR_PALETTES = {
            {new Color(30, 60, 114), new Color(42, 82, 152)},    // Deep Navy
            {new Color(24, 76, 120), new Color(80, 161, 230)},    // Ocean Blue
            {new Color(44, 62, 80), new Color(76, 161, 175)},     // Midnight Slate
            {new Color(78, 84, 200), new Color(143, 148, 251)},   // Royal Purple
            {new Color(19, 78, 94), new Color(113, 178, 128)}     // Pine Teal
    };

    @Override
    public CaptchaVO generateCaptcha() {
        if (!securityProperties.isCaptchaEnabled()) {
            return CaptchaVO.builder()
                    .enabled(false)
                    .build();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int targetX = random.nextInt(70, IMG_WIDTH - PIECE_SIZE - 20);
        int targetY = random.nextInt(20, IMG_HEIGHT - PIECE_SIZE - 20);

        BufferedImage bgImg = generateBackgroundImage(random);

        // 创建滑块拼图图层（与原图尺寸一致，拼图置于 (0, targetY)）
        BufferedImage sliderImg = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Shape sliderShape = createPuzzleShape(0, targetY, PIECE_SIZE);

        Graphics2D g2Slider = sliderImg.createGraphics();
        g2Slider.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Slider.setClip(sliderShape);
        g2Slider.drawImage(bgImg, -targetX, 0, null);
        g2Slider.setClip(null);
        g2Slider.setColor(new Color(255, 255, 255, 230));
        g2Slider.setStroke(new BasicStroke(1.8f));
        g2Slider.draw(sliderShape);
        g2Slider.dispose();

        // 在背景图上挖空凹槽（半透明阴影与虚线框）
        Shape targetShape = createPuzzleShape(targetX, targetY, PIECE_SIZE);
        Graphics2D g2Bg = bgImg.createGraphics();
        g2Bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Bg.setColor(new Color(0, 0, 0, 160));
        g2Bg.fill(targetShape);
        g2Bg.setColor(new Color(255, 255, 255, 200));
        g2Bg.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f));
        g2Bg.draw(targetShape);
        g2Bg.dispose();

        String token = IdUtil.fastSimpleUUID();
        cacheService.put(CacheKeyConstants.captcha(token), targetX, CAPTCHA_TTL);

        return CaptchaVO.builder()
                .token(token)
                .enabled(true)
                .backgroundImage(toBase64DataUrl(bgImg))
                .sliderImage(toBase64DataUrl(sliderImg))
                .y(targetY)
                .build();
    }

    @Override
    public void verifyCaptcha(String token, Integer userX) {
        if (!securityProperties.isCaptchaEnabled()) {
            return;
        }

        if (StrUtil.isBlank(token) || userX == null) {
            throw new BusinessException("请完成滑块安全验证");
        }

        String cacheKey = CacheKeyConstants.captcha(token);
        Integer targetX = cacheService.get(cacheKey, Integer.class);
        cacheService.evict(cacheKey);

        if (targetX == null) {
            throw new BusinessException("验证码已过期，请重新刷新");
        }

        if (Math.abs(targetX - userX) > TOLERANCE) {
            throw new BusinessException("滑块拼图未对准，请重新尝试");
        }
    }

    private BufferedImage generateBackgroundImage(ThreadLocalRandom random) {
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 随机渐变背景
        Color[] palette = COLOR_PALETTES[random.nextInt(COLOR_PALETTES.length)];
        GradientPaint gradient = new GradientPaint(0, 0, palette[0], IMG_WIDTH, IMG_HEIGHT, palette[1]);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);

        // 绘制科技感网格微弱线条
        g2.setColor(new Color(255, 255, 255, 18));
        for (int x = 0; x < IMG_WIDTH; x += 20) {
            g2.drawLine(x, 0, x, IMG_HEIGHT);
        }
        for (int y = 0; y < IMG_HEIGHT; y += 20) {
            g2.drawLine(0, y, IMG_WIDTH, y);
        }

        // 随机几何光晕与气泡
        for (int i = 0; i < 6; i++) {
            int radius = random.nextInt(20, 80);
            int circleX = random.nextInt(-20, IMG_WIDTH);
            int circleY = random.nextInt(-20, IMG_HEIGHT);
            int alpha = random.nextInt(20, 60);
            g2.setColor(new Color(255, 255, 255, alpha));
            g2.fillOval(circleX, circleY, radius * 2, radius * 2);
        }

        // 随机装饰线条
        for (int i = 0; i < 4; i++) {
            g2.setColor(new Color(255, 255, 255, random.nextInt(30, 70)));
            int x1 = random.nextInt(IMG_WIDTH);
            int y1 = random.nextInt(IMG_HEIGHT);
            int x2 = random.nextInt(IMG_WIDTH);
            int y2 = random.nextInt(IMG_HEIGHT);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.dispose();
        return image;
    }

    private Shape createPuzzleShape(int x, int y, int s) {
        Path2D path = new Path2D.Double();
        int r = 7; // 凸起凹陷半径
        double halfS = s / 2.0;

        path.moveTo(x, y);
        // 顶边：向外圆形凸起
        path.lineTo(x + halfS - r, y);
        path.quadTo(x + halfS, y - r * 1.5, x + halfS + r, y);
        path.lineTo(x + s, y);

        // 右边：向外圆形凸起
        path.lineTo(x + s, y + halfS - r);
        path.quadTo(x + s + r * 1.5, y + halfS, x + s, y + halfS + r);
        path.lineTo(x + s, y + s);

        // 底边：向内圆形凹陷
        path.lineTo(x + halfS + r, y + s);
        path.quadTo(x + halfS, y + s - r * 1.5, x + halfS - r, y + s);
        path.lineTo(x, y + s);

        // 左边：直线闭合
        path.closePath();
        return path;
    }

    private String toBase64DataUrl(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            log.error("生成验证码图片失败", e);
            throw new BusinessException("生成验证码图片异常");
        }
    }
}
