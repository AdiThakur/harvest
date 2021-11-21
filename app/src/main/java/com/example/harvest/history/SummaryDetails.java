package com.example.harvest.history;

public class SummaryDetails
{
	public double totalWeight = 0.0;
	public int totalUnits = 0;
	public int totalHarvests = 0;

	public SummaryDetails(double totalWeight, int totalUnits, int totalHarvests)
	{
		this.totalWeight = totalWeight;
		this.totalUnits = totalUnits;
		this.totalHarvests = totalHarvests;
	}
}
