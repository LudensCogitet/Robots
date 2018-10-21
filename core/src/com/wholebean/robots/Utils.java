package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by john on 8/28/18.
 */

public class Utils {
    public static int indexFromCoords(Vector2 coords, int rowWidth) {
        return (int) (coords.x + (coords.y * rowWidth));
    }

    public static int indexFromCoords(int x, int y, int rowWidth) {
        return (x + (y * rowWidth));
    }

    public static Vector2 coordsFromIndex(int index, int rowWidth) {
        int x = index % rowWidth;
        int y = (index - x) / rowWidth;
        return new Vector2(x, y);
    }

    public static Array<Integer> getShuffledRange(int size) {
        Array<Integer> range = new Array<Integer>(size);

        for(int i = 0; i < size; i++) {
            range.add(i);
        }

        range.shuffle();

        return range;
    }

    public static TextureRegion[][] fixBleeding(TextureRegion[][] region) {
        for (TextureRegion[] array : region) {
            for (TextureRegion texture : array) {
                fixBleeding(texture);
            }
        }

        return region;
    }

    public static TextureRegion fixBleeding(TextureRegion region) {
        float fix = 0.01f;

        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight); // Trims
        // region
        return region;
    }

    public static <T> Array<T> shuffleArray(RandomXS128 rand, Array<T> array) {
        int currentIndex = array.size;
        int randomIndex;

        while(0 != currentIndex) {
            randomIndex = MathUtils.floor(rand.nextFloat() * currentIndex);
            currentIndex--;

            array.swap(currentIndex, randomIndex);
        }

        return array;
    }
}