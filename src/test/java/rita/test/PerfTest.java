package rita.test;

import rita.RiTa;

public class PerfTest {
	public static void main(String[] args) {
		String lstr = "Lorem ipsum dolor sit amet, (consectetur adipiscing elit) morbi ullamcorper porttitor lorem, in faucibus velit ultrices nec. Curabitur convallis luctus felis, sed posuere turpis mollis quis. Suspendisse euismod vel tellus sit amet tempus. Nullam pretium tincidunt pellentesque. Vestibulum tempus eget eros non dignissim. Nullam faucibus et augue a commodo. Curabitur tellus est, elementum sit amet finibus a, posuere in nunc. In libero metus, tempor nec tincidunt eu, vulputate a ex. Aliquam id tincidunt sapien. In pharetra condimentum lacus, non congue arcu tempor nec. Nullam faucibus odio id diam dapibus volutpat sed in quam. Vivamus ex quam, efficitur sit amet ante eu, congue blandit arcu. Suspendisse molestie sit amet diam ac tristique.";
		RiTa.evaluate(lstr);
	}
}
