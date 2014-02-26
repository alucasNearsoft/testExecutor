import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class CalculateE
{
	final static int LASTITER = 17;
	public static void main(String[] args)
	{
		// first obtains an executor by calling Executors’ newFixedThreadPool() method.
		ExecutorService executor = Executors.newFixedThreadPool(1);

		// then instantiates an anonymous class that implements Callable
		Callable<BigDecimal> callable;
		callable = new Callable<BigDecimal>() {
			/* The callable’s call() method calculates e by evaluating the mathematical power series e =
			 * 1/0!+1/1!+1/2!+…. This series can be evaluated by summing 1/n!, where n ranges from 0 to infinity.
			 * @see java.util.concurrent.Callable#call()
			 */
			public BigDecimal call()
			{
				// first instantiates java.math.MathContext to encapsulate a precision (number of digits) and a
				// rounding mode. Author chose 100 as an upper limit on e’s precision and HALF_UP as the rounding mode.
				MathContext mc = new MathContext(100, RoundingMode.HALF_UP);
				// next initializes a java.math.BigDecimal local variable named result to BigDecimal.ZERO.
				BigDecimal result = BigDecimal.ZERO;
				// then enters a loop ...
				for (int i = 0; i <= LASTITER; i++)
				{
					// ... that calculates a factorial,
					BigDecimal factorial = factorial(new BigDecimal(i));
					// ... divides BigDecimal.ONE by the factorial,
					BigDecimal res = BigDecimal.ONE.divide(factorial, mc);
					// ... and adds the	division result to result
					result = result.add(res);
				}
				return result;
			}
			public BigDecimal factorial(BigDecimal n)
			{
				if (n.equals(BigDecimal.ZERO))
					return BigDecimal.ONE;
				else
					return n.multiply(factorial(n.subtract(BigDecimal.ONE)));
			}
		};
		
		// and submits this task to the executor, receiving a Future instance in response.
		Future<BigDecimal> taskFuture = executor.submit(callable);
		try
		{
			// After submitting a task, a thread typically does some other work until it needs to obtain the task’s
			// result. Author have chosen to simulate this work by having the main thread repeatedly output a waiting
			// message until the Future instance’s isDone() method returns true. (In a realistic application, it would
			// be avoided this looping.)
			while (!taskFuture.isDone())
				System.out.println("waiting");
			// At this point, the main thread calls the instance’s get() method to obtain the result,
			// which is then output.
			System.out.println(taskFuture.get());
		}
		catch(ExecutionException ee)
		{
			System.err.println("task threw an exception");
			System.err.println(ee);
		}
		catch(InterruptedException ie)
		{
			System.err.println("interrupted while waiting");
		}
		// It is important to shut down the executor after it completes; otherwise, the application might not end.
		// The application accomplishes this task by calling shutdownNow().
		executor.shutdownNow();
	}
}