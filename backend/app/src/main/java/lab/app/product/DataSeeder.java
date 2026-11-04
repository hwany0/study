package lab.app.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** 앱 시작 시 상품 테이블이 비어 있으면 더미 데이터를 적재한다. */
@Component
public class DataSeeder implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final boolean enabled;
    private final int count;

    public DataSeeder(JdbcTemplate jdbc,
                      @Value("${lab.seed.enabled}") boolean enabled,
                      @Value("${lab.seed.product-count}") int count) {
        this.jdbc = jdbc;
        this.enabled = enabled;
        this.count = count;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) return;
        Long existing = jdbc.queryForObject("select count(*) from product", Long.class);
        if (existing != null && existing > 0) return;

        var rnd = ThreadLocalRandom.current();
        List<Object[]> batch = new ArrayList<>(1000);
        for (int i = 1; i <= count; i++) {
            batch.add(new Object[]{"product-" + i, 1000 + rnd.nextInt(99000),
                    rnd.nextInt(1000), rnd.nextInt(1, 101)});
            if (batch.size() == 1000) {
                insert(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) insert(batch);
    }

    private void insert(List<Object[]> batch) {
        jdbc.batchUpdate("insert into product(name, price, stock, category_id) values (?,?,?,?)", batch);
    }
}
