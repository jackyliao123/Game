package game;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class TextureLoader {
    private int textureId;
    public final int tileWidth;
    public final int tileHeight;
    public final int width;
    public final int height;

    public TextureLoader() {
        tileWidth = 0;
        tileHeight = 0;
        width = 1;
        height = 1;
    }

    public TextureLoader(InputStream input, int tileWidth, int tileHeight, int width, int height) throws IOException {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.width = width;
        this.height = height;
        bindTexture(input);
    }

    public double getTextureX(int textureIndex, double x) {
        return (textureIndex % width + x) / width;
    }

    public double getTextureY(int textureIndex, double y) {
        return ((int) (textureIndex / width) + y) / height;
    }

    public int getTextureId() {
        return textureId;
    }

    public void bindTexture(InputStream input) throws IOException {
        BufferedImage image = ImageIO.read(input);
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int i = 0; i < pixels.length; i++) {
            int rgba = pixels[i];
            buffer.put((byte) (rgba >> 16 & 0xFF)).put((byte) (rgba >> 8 & 0xFF)).put((byte) (rgba & 0xFF)).put((byte) (rgba >> 24 & 0xFF));
        }
        buffer.flip();
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

}
