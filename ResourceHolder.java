public interface ResourceHolder {
	public int getResourceCount();
	public int getResource(int r);
	public int transfer(ResourceHolder other, int resource, int amount);
	public int reduce(int resource, int amount);
}