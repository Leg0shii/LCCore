package de.legoshi.lccore.command;

import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Register
@Command(names = {"coords", "coordinates"}, permission = "coords", desc = "[precision]")
public class CoordinatesCommand implements CommandClass {

    @Command(names = "")
    public void coords(CommandSender sender, @OptArg Integer precision) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player player = (Player)sender;
        Location loc = player.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float f = loc.getYaw();

        int finalPrecision = 8;
        if(precision != null) {
            if(precision < 0 || precision > 20) {
                MessageUtil.send(Message.CURRENT_LOC_NOT_IN_RAGE, sender);
                return;
            }

            finalPrecision = precision;
        }

        MessageUtil.send("§6--- §lCoordinates §6---", player, false);

        MessageUtil.send("§6X: §f" + trimTrailingZeros(x, finalPrecision), player, false);
        MessageUtil.send("§6Y: §f" + trimTrailingZeros(y, finalPrecision), player, false);
        MessageUtil.send("§6Z: §f" + trimTrailingZeros(z, finalPrecision), player, false);
        MessageUtil.send("§6F: §f" + trimTrailingZeros(f, finalPrecision), player, false);

    }

    private String trimTrailingZeros(double value, int precision) {
        return new BigDecimal(Double.toString(value))
                .setScale(precision, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

    private String trimTrailingZeros(float value, int precision) {
        return new BigDecimal(Float.toString(value))
                .setScale(precision, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }
}
