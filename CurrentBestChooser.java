import net.sourceforge.interval.ia_math.RealInterval;

/*
 * Basic implementation of the Chooser
 * Choose the current best box
 */

public class CurrentBestChooser extends Chooser {

	public CurrentBestChooser(WorkList list) {
		this.list = list;
	}

	@Override
	public Box extractNext() {
		return list.getCurrentBest();
	}

	private WorkList list;
}
