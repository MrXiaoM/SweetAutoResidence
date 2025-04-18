package res.ext;

import com.bekvon.bukkit.residence.text.Language;
import com.bekvon.bukkit.residence.Residence;

public class ResidenceExt {
    public static Language getLM() {
        return Residence.getInstance().getLM();
    }
}
