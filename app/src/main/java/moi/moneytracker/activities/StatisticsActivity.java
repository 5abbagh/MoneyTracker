package moi.moneytracker.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import moi.moneytracker.DatabaseHandler;
import moi.moneytracker.MTApp;
import moi.moneytracker.R;
import moi.moneytracker.fragments.AlertFragment;
import moi.moneytracker.models.Account;
import moi.moneytracker.models.Transaction;

public class StatisticsActivity extends AppCompatActivity implements AlertFragment.AlertDialogListener{

    Button normalBtn;
    Button compareBtn;

    Spinner accountsSpin;

    Spinner yearsSpin;
    Spinner monthsSpin;
    Spinner year2Spin;
    Spinner month2Spin;

    ToggleButton showTransactionsToggle;
    LinearLayout transactionsLn;
    TransactionsListAdapter transAdapter;

    TextView graphsLabel;
    PieChart monthPie;
    PieChart yearPie;
    LineChart yearLine;
    BarChart twoMonthsBar;
    LineChart twoYearsLine;


    DatabaseHandler db;
    List<Account> accounts;
    List<Transaction> transactions;
    String [] curLocalcats;
    String [] enLocalcats;

    boolean modeIsNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        db = MTApp.getDatabase();

        curLocalcats = getResources().getStringArray(R.array.expenseCategories);
        enLocalcats = MTApp.getLocalizedResources(this,new Locale("en")).getStringArray(R.array.expenseCategories);

        Log.d("xyz:","size is " + enLocalcats.length);

        transactions = new ArrayList<Transaction>();

        normalBtn = (Button) findViewById(R.id.normalBtn);
        compareBtn = (Button) findViewById(R.id.compareBtn);
        modeIsNormal = true;

        graphsLabel = (TextView) findViewById(R.id.graphsLabel);
        monthPie = (PieChart) findViewById(R.id.monthPie);
        yearPie = (PieChart) findViewById(R.id.yearPie);
        yearLine = (LineChart) findViewById(R.id.yearLine);
        twoYearsLine = (LineChart) findViewById(R.id.twoYearsLine);
        twoMonthsBar = (BarChart) findViewById(R.id.twoMonthsBar);

        monthPie.setNoDataText("");
        yearPie.setNoDataText("");
        yearLine.setNoDataText("");
        twoYearsLine.setNoDataText("");
        twoMonthsBar.setNoDataText("");

        transactionsLn = (LinearLayout) findViewById(R.id.transactionsListView);
        showTransactionsToggle = (ToggleButton) findViewById(R.id.showTransactionsToggle);
        accountsSpin = (Spinner) findViewById(R.id.accountSpinner);
        monthsSpin = (Spinner) findViewById(R.id.monthSpinner);
        month2Spin = (Spinner) findViewById(R.id.month2Spin);
        yearsSpin = (Spinner) findViewById(R.id.yearSpinner);
        year2Spin = (Spinner) findViewById(R.id.year2Spin);

        toggleMode(false);

        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMode(false);
                // make compare fields invisible
            }
        });

        compareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMode(true);
                // make compare fields visible
            }
        });


        monthPie.setVisibility(View.GONE);
        yearPie.setVisibility(View.GONE);
        yearLine.setVisibility(View.GONE);
        twoYearsLine.setVisibility(View.GONE);
        twoMonthsBar.setVisibility(View.GONE);
        transAdapter = new TransactionsListAdapter();

        accounts = db.getAccounts();
        String[] accountDropItems = new String[accounts.size()];
        int i = 0;
        for( Account acc : accounts)
        {
            accountDropItems[i] = acc.getAccountName() + ": " + acc.getBalance() + " " + acc.getCurrency();
            i++;
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,accountDropItems );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpin.setAdapter(adapter);
        accountsSpin.setSelection(0);

        accountsSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String[] yearsArray = db.getAccountYears(accounts.get(i));
                ArrayAdapter<CharSequence> yearsAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                        android.R.layout.simple_spinner_item,yearsArray );
                yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                yearsSpin.setAdapter(yearsAdapter);
                yearsSpin.setSelection(0);
                year2Spin.setAdapter(yearsAdapter);
                year2Spin.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        accountsSpin.getOnItemSelectedListener().onItemSelected(null,null,0,0);


        String[] monthsArray = getResources().getStringArray(R.array.monthsArrayEx);
        ArrayAdapter monthAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_spinner_item,monthsArray);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpin.setAdapter(monthAdapter);
        monthsSpin.setSelection(0);
        month2Spin.setAdapter(monthAdapter);
        month2Spin.setSelection(0);

        yearsSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkEntries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        year2Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkEntries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        monthsSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkEntries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        month2Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkEntries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        checkEntries();

        showTransactionsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateTransactions();
                    inflateTransLayout();
                    transactionsLn.setVisibility(View.VISIBLE);
                } else {
                    transactionsLn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void inflateTransLayout()
    {
        transactionsLn.removeAllViews();
        for( int i = 0; i < transactions.size(); i++ )
        {
            View view = transAdapter.getView(i,null,transactionsLn);
            transactionsLn.addView(view);
        }
    }

    private void toggleMode( boolean compare )
    {
        if (compare)
        {
            compareBtn.setTextColor(Color.BLACK);
            normalBtn.setTextColor(Color.WHITE);

            removeShowViews(false, (LinearLayout)findViewById(R.id.secondDateLay));

            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.statConstraint);
            ConstraintSet constraintSet = new ConstraintSet();
            LinearLayout secondDateLay = (LinearLayout) findViewById(R.id.secondDateLay);

            constraintSet.clone(constraintLayout);
            constraintSet.connect(graphsLabel.getId(), ConstraintSet.TOP, secondDateLay.getId(), ConstraintSet.BOTTOM, 100);
            constraintSet.applyTo(constraintLayout);

            int curMonth = Calendar.getInstance().get(Calendar.MONTH);
            monthsSpin.setSelection(curMonth + 1);
            month2Spin.setSelection(curMonth == 0 ? 2 : curMonth );
        }
        else
        {
            compareBtn.setTextColor(Color.WHITE);
            normalBtn.setTextColor(Color.BLACK);

            removeShowViews(true, (LinearLayout)findViewById(R.id.secondDateLay));

            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.statConstraint);
            ConstraintSet constraintSet = new ConstraintSet();
            LinearLayout firstDateLay = (LinearLayout) findViewById(R.id.firstDateLay);

            constraintSet.clone(constraintLayout);
            constraintSet.connect(graphsLabel.getId(), ConstraintSet.TOP, firstDateLay.getId(), ConstraintSet.BOTTOM, 100);
            constraintSet.applyTo(constraintLayout);
        }

        modeIsNormal = !compare;
        checkEntries();
    }

    private void checkEntries()
    {
        monthPie.setVisibility(View.GONE);
        yearPie.setVisibility(View.GONE);
        yearLine.setVisibility(View.GONE);
        twoMonthsBar.setVisibility(View.GONE);
        twoYearsLine.setVisibility(View.GONE);
        graphsLabel.setVisibility(View.GONE);

        Account account = accountsSpin.getSelectedItem() == null ? null : accounts.get(accountsSpin.getSelectedItemPosition());
        int year = yearsSpin.getSelectedItem() == null ? -1 : Integer.valueOf((String)yearsSpin.getSelectedItem());
        int month = monthsSpin.getSelectedItemPosition();

        if ( modeIsNormal )
        {

            if (account != null && year != -1 &&  month != -1 )
            {
                if (month == 0)
                {
                    graphNormalYear(account,year);
                    showToggleButton(false);
                }
                else
                {
                    graphNormalMonth(account, year, month);
                    showToggleButton(true);
                }
            }
            else
            {
                graphsLabel.setText(R.string.noGraphsAvail);
                graphsLabel.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            showToggleButton(false);
            // do compare mode checking
            int year2 = year2Spin.getSelectedItem() == null ? -1 : Integer.valueOf((String)year2Spin.getSelectedItem());
            int month2 = month2Spin.getSelectedItemPosition();
            if ( account != null && year != -1 &&  month != -1 && year2 != -1 &&  month2 != -1 )
            {
                if (year == year2 && month == month2 )
                {
                    graphsLabel.setText(R.string.sameStatDatePrompt);
                    graphsLabel.setVisibility(View.VISIBLE);
                }
                else if( (month == 0 && month2 != 0) || ( month2 == 0 && month != 0) )
                {
                    graphsLabel.setText(R.string.difStatLength);
                    graphsLabel.setVisibility(View.VISIBLE);
                }
                else
                {
                    if (month == 0)
                        graphCompareYear(account,year,year2);
                    else
                        graphCompareMonth(account,year,month,year2,month2);
                }
            }
            else
            {
                graphsLabel.setText(R.string.noGraphsAvail);
                graphsLabel.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateTransactions()
    {
        int accountId = accounts.get(accountsSpin.getSelectedItemPosition()).getAccountId();
        int year = yearsSpin.getSelectedItem() == null ? -1 : Integer.valueOf((String)yearsSpin.getSelectedItem());
        int month = monthsSpin.getSelectedItemPosition();
        transactions = db.getMonthTransactions(accountId, year, month,false);
    }

    private void showToggleButton(boolean show)
    {
        showTransactionsToggle.setChecked(false);
        if (show)
        {
            showTransactionsToggle.setVisibility(View.VISIBLE);
        }
        else
        {
            showTransactionsToggle.setVisibility(View.GONE);
        }
    }

    private void graphNormalMonth( Account account, int year, int month )
    {
        monthPie.setUsePercentValues(true);
        monthPie.setRotationEnabled(false);
        monthPie.setDrawHoleEnabled(true);
        monthPie.setHoleRadius(15);
        monthPie.setTransparentCircleRadius(20);
        monthPie.setDescription(getString(R.string.expensesCategories));
        monthPie.setDrawSliceText(false);

        Legend legend = monthPie.getLegend();
//        pieChart.setClickable(false);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);

        ArrayList<String> sumCats = db.getMonthCategoriesExpenses(account.getAccountId(),year,month);
        ArrayList<Entry> totals = new ArrayList<Entry>();
        ArrayList<String> cats = new ArrayList<String>();
        float total = 0;
        for (int i = 0; i < sumCats.size(); i = i + 2)
        {
            total += Float.valueOf(sumCats.get(i));
        }

        for (int i = 0; i < sumCats.size(); i++)
        {
            if (i % 2 == 0)
                totals.add(new Entry(Float.valueOf(sumCats.get(i))*100/total,i/2));
            else
            {
                int index = MTApp.getIndexOf(sumCats.get(i),enLocalcats);
                if (index != -1)
                {
                    cats.add(curLocalcats[index]);
                }
            }
        }


        PieDataSet pieDataSet = new PieDataSet(totals,"");
        pieDataSet.setSliceSpace(2);
//        pieDataSet.setSelectionShift(15);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
//        for (int c : ColorTemplate.rgb("#3F89BA"))
            colors.add(Color.rgb(99, 57, 116));

        pieDataSet.setColors(colors);
        PieData pieData = new PieData(cats,pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12);
        monthPie.setData(pieData);
        monthPie.setVisibility(View.VISIBLE);
        monthPie.animateY(1000);
        monthPie.invalidate();
    }

    private void graphNormalYear( Account account, int year )
    {
        yearPie.setUsePercentValues(true);
        yearPie.setRotationEnabled(false);
        yearPie.setDrawHoleEnabled(true);
        yearPie.setHoleRadius(15);
        yearPie.setTransparentCircleRadius(20);
        yearPie.setDescription(getString(R.string.yearExCat));
        yearPie.setDrawSliceText(false);

        Legend legend = yearPie.getLegend();
//        pieChart.setClickable(false);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);

        ArrayList<String> sumCats = db.getYearCategoriesExpenses(account.getAccountId(),year);
        ArrayList<Entry> totals = new ArrayList<Entry>();
        ArrayList<String> cats = new ArrayList<String>();
        float total = 0;
        for (int i = 0; i < sumCats.size(); i = i + 2)
        {
            total += Float.valueOf(sumCats.get(i));
        }

        for (int i = 0; i < sumCats.size(); i++)
        {
            if (i % 2 == 0)
                totals.add(new Entry(Float.valueOf(sumCats.get(i))*100/total,i/2));
            else
            {
                int index = MTApp.getIndexOf(sumCats.get(i),enLocalcats);
                if (index != -1)
                {
                    cats.add(curLocalcats[index]);
                }
            }
        }


        PieDataSet pieDataSet = new PieDataSet(totals,"");
        pieDataSet.setSliceSpace(2);
//        pieDataSet.setSelectionShift(15);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
//        for (int c : ColorTemplate.rgb("#3F89BA"))
        colors.add(Color.rgb(99, 57, 116));

        pieDataSet.setColors(colors);
        PieData pieData = new PieData(cats,pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12);
        yearPie.setData(pieData);
        yearPie.setVisibility(View.VISIBLE);
        yearPie.animateY(1000);
        yearPie.invalidate();

        //////////////////////////////////////////////////////


        ArrayList<String> monthlyEx = db.getYearMonthlyExpenses(account.getAccountId(),year);

        List<Entry> entries = new ArrayList<Entry>();

        String[] months = getResources().getStringArray(R.array.monthsArray);
        for( int i = 1; i < 13; i++ )
        {
            entries.add(new Entry(Float.valueOf(monthlyEx.get(i-1)),i-1));
        }
        LineDataSet dataSet = new LineDataSet(entries,getString(R.string.monthlyExpensesIn) + " " + year);

        dataSet.setColor(Color.rgb(63,137,186));
        dataSet.setCircleColor(Color.rgb(186,137,63));
        LineData lineData = new LineData(months,dataSet);
        yearLine.setData(lineData);
        yearLine.setDoubleTapToZoomEnabled(false);
        yearLine.setDescription("");
        yearLine.setVisibility(View.VISIBLE);
        yearLine.animateX(1000);
        yearLine.invalidate(); // refresh
//        setNoDataText(String text)

    }

    private void graphCompareMonth( Account account, int year, int month, int year2, int month2)
    {
        Log.d("xyz:","graphing two months");

        ArrayList<String> sumCats = db.getMonthCategoriesExpenses(account.getAccountId(),year,month);
        ArrayList<String> sumCats2 = db.getMonthCategoriesExpenses(account.getAccountId(),year2,month2);


        ArrayList<ArrayList<Float>> values = new ArrayList<>();
        values.add(new ArrayList<Float>(Collections.nCopies(enLocalcats.length, 0f)));
        values.add(new ArrayList<Float>(Collections.nCopies(enLocalcats.length, 0f)));


        for (int i = 0; i < sumCats.size(); i = i + 2)
        {
            float amount = Float.valueOf(sumCats.get(i));
            int index = MTApp.getIndexOf(sumCats.get(i+1),enLocalcats);
            values.get(0).set(index,amount);
        }

        for (int i = 0; i < sumCats2.size(); i = i + 2)
        {
            float amount = Float.valueOf(sumCats2.get(i));
            int index = MTApp.getIndexOf(sumCats2.get(i+1),enLocalcats);
            values.get(1).set(index,amount);
        }


        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();

        for(int i = 0; i < values.get(0).size(); i++) {
            entriesGroup1.add(new BarEntry(values.get(0).get(i),i));
            entriesGroup2.add(new BarEntry(values.get(1).get(i),i));
        }

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        ArrayList<IBarDataSet> dataSets = new ArrayList<>(2);
        dataSets.add(new BarDataSet(entriesGroup1, monthsSpin.getSelectedItem().toString() + ", " + year));
        dataSets.add(new BarDataSet(entriesGroup2, month2Spin.getSelectedItem().toString() + ", " + year2));
        ((BaseDataSet)dataSets.get(0)).setColor(Color.rgb(63, 137, 186));
        ((BaseDataSet)dataSets.get(1)).setColor(Color.rgb(186, 137, 63));

        BarData data = new BarData(curLocalcats, dataSets);
//        data.setBarWidth(barWidth); // set the width of each bar
        data.setGroupSpace(groupSpace);

        twoMonthsBar.setData(data);
        twoMonthsBar.setDescription("");
        twoMonthsBar.setVisibility(View.VISIBLE);
        twoMonthsBar.animateY(1000);
//        twoMonthsBar.groupBars(1980f, groupSpace, barSpace); // perform the "explicit" grouping
        twoMonthsBar.invalidate(); // refresh
    }

    private void graphCompareYear( Account account, int year, int year2)
    {
        Log.d("xyz:","graphing two years");

        ArrayList<String> monthlyEx = db.getYearMonthlyExpenses(account.getAccountId(),year);
        ArrayList<String> monthlyEx2 = db.getYearMonthlyExpenses(account.getAccountId(),year2);

        List<Entry> entries = new ArrayList<Entry>();
        List<Entry> entries2 = new ArrayList<Entry>();

        String[] months = getResources().getStringArray(R.array.monthsArray);
        for( int i = 1; i < 13; i++ )
        {
            entries.add(new Entry(Float.valueOf(monthlyEx.get(i-1)),i-1));
            entries2.add(new Entry(Float.valueOf(monthlyEx2.get(i-1)),i-1));
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add( new LineDataSet(entries,getString(R.string.monthlyExpensesIn) + " " + year));
        dataSets.add( new LineDataSet(entries2,getString(R.string.monthlyExpensesIn) + " " + year2));

        ((LineDataSet)dataSets.get(0)).setColor(Color.rgb(63, 137, 186));
        ((LineDataSet)dataSets.get(0)).setCircleColor(Color.rgb(186,137,63));
        ((LineDataSet)dataSets.get(1)).setColor(Color.rgb(186,137,63));
        ((LineDataSet)dataSets.get(1)).setCircleColor(Color.rgb(63,137,186));

        LineData lineData = new LineData(months,dataSets);
        yearLine.setData(lineData);
        yearLine.setDoubleTapToZoomEnabled(false);
        yearLine.setDescription("");
        yearLine.setVisibility(View.VISIBLE);
        yearLine.animateX(1000);
        yearLine.invalidate(); // refresh
    }



    ///////
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object toDelete) {
        if (toDelete instanceof Transaction )
            deleteTransaction( (Transaction)toDelete );
    }

    private void deleteTransaction(Transaction toDelete)
    {
        db.deleteTransaction(toDelete);
        updateTransactions();
        inflateTransLayout();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Object toDelete) {}

    ////////

    private class TransactionsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return transactions.size();
        }

        @Override
        public String getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return accounts.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container)
        {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.ex_list_item, container, false);
            }

            final Transaction transaction = transactions.get(position) ;

            TextView transDetails = (TextView) convertView.findViewById(R.id.transDetails);
            ImageButton deleteTransBtn = (ImageButton) convertView.findViewById(R.id.deleteTransBtn);

            transDetails.setText(transaction.getAmount() + " : " + transaction.getNotes());
            deleteTransBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment df = new AlertFragment();
                    ((AlertFragment)df).setMessage(getString(R.string.delTransAlertMsg));
                    ((AlertFragment)df).setToDeleteObject(transaction);
                    df.show(getSupportFragmentManager(),"deleteTransaction");
                }
            });
            return convertView;
        }
    }

    // Recursive method to disable/enable views inside a ViewGroup.
    private void removeShowViews(boolean remove, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            if (remove)
                child.setVisibility(View.GONE);
            else
                child.setVisibility(View.VISIBLE);
            if (child instanceof ViewGroup){
                removeShowViews(remove, (ViewGroup)child);
            }
        }
    }

}
