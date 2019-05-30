package fjy.ins;

import android.content.*;
import android.net.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.youth.banner.loader.*;
import com.bumptech.glide.load.resource.bitmap.*;

public class GlideImageLoader extends ImageLoader
{
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) 
	{   
        Glide.with(context)
			.load(path)
			.crossFade(600)
			.transform(new CenterCrop(context), new GlideRoundTransform(context, 12))
			.skipMemoryCache(true)
			.into(imageView);
    }
}
