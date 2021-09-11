package fr.structure.bo;

public class Note
{

	private int note;
	private int velocity;
	private float duration;
	
	public int getNote()
	{

		return this.note;
	}
	public void setNote(int _note)
	{

		this.note = _note;
	}

	public int getVelocity()
	{
		
		return this.velocity;
	}
	public void setVelocity(int _velocity)
	{

		this.velocity = _velocity;
	}

	public float getDuration()
	{

		return this.duration;
	}
	public void setDuration(float _duration)
	{

		this.duration = _duration;
	}

	public Note()
	{
		
		super();
	}
	
	public Note( int _note, int _velocity, float _duration)
	{

		super();

		this.note = _note;
		this.velocity = _velocity;
		this.duration = _duration;

	}
	
}
