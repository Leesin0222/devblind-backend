-- 기술 스택 초기 데이터
INSERT INTO tech_stacks (name) VALUES 
-- 프론트엔드
('JavaScript'), ('TypeScript'), ('React'), ('Vue.js'), ('Angular'), 
('Next.js'), ('Nuxt.js'), ('HTML/CSS'), ('Sass/SCSS'), ('Tailwind CSS'),
('Bootstrap'), ('Material-UI'), ('Ant Design'), ('Chakra UI'), ('Styled Components'),
('Redux'), ('Zustand'), ('Recoil'), ('Jotai'), ('Vuex'), ('Pinia'),
('Webpack'), ('Vite'), ('Rollup'), ('Parcel'), ('Babel'), ('ESLint'), ('Prettier'),
('Jest'), ('Vitest'), ('Cypress'), ('Playwright'), ('Storybook'), ('Framer Motion'),

-- 백엔드
('Java'), ('Spring Boot'), ('Spring Security'), ('Spring Data JPA'), ('Spring Cloud'),
('Python'), ('Django'), ('FastAPI'), ('Flask'), ('Celery'), ('Pandas'), ('NumPy'),
('Node.js'), ('Express.js'), ('NestJS'), ('Koa'), ('Socket.io'), ('PM2'),
('Go'), ('Gin'), ('Echo'), ('Fiber'), ('GORM'), ('Cobra'),
('C#'), ('.NET'), ('ASP.NET Core'), ('Entity Framework'), ('SignalR'),
('PHP'), ('Laravel'), ('Symfony'), ('CodeIgniter'), ('WordPress'),
('Ruby'), ('Ruby on Rails'), ('Sinatra'), ('RSpec'), ('Capybara'),
('Rust'), ('Actix'), ('Rocket'), ('Axum'), ('Tokio'), ('Serde'),

-- 데이터베이스
('PostgreSQL'), ('MySQL'), ('MongoDB'), ('Redis'), ('Elasticsearch'), 
('Oracle'), ('MariaDB'), ('SQLite'), ('MSSQL'), ('Cassandra'), ('DynamoDB'),
('Neo4j'), ('InfluxDB'), ('CouchDB'), ('RethinkDB'), ('ArangoDB'), ('TimescaleDB'),

-- 클라우드/DevOps
('AWS'), ('GCP'), ('Azure'), ('Docker'), ('Kubernetes'), 
('Jenkins'), ('GitHub Actions'), ('Terraform'), ('Ansible'), ('Puppet'), ('Chef'),
('Prometheus'), ('Grafana'), ('ELK Stack'), ('Jaeger'), ('Zipkin'), ('Istio'),
('Helm'), ('ArgoCD'), ('Flux'), ('Tekton'), ('Spinnaker'), ('Vault'), ('Consul'),
('AWS Lambda'), ('Azure Functions'), ('Google Cloud Functions'), ('Serverless Framework'),

-- 모바일
('React Native'), ('Flutter'), ('iOS/Swift'), ('Android/Kotlin'), ('Xamarin'),
('Ionic'), ('Cordova'), ('PhoneGap'), ('Unity'), ('Unreal Engine'), ('Cocos2d'),

-- AI/ML/Data
('Machine Learning'), ('Data Science'), ('Deep Learning'), ('TensorFlow'), ('PyTorch'),
('Scikit-learn'), ('Keras'), ('OpenCV'), ('NLTK'), ('SpaCy'), ('Hugging Face'),
('Apache Spark'), ('Hadoop'), ('Kafka'), ('Airflow'), ('Dagster'), ('Prefect'),
('Jupyter'), ('Colab'), ('Databricks'), ('Snowflake'), ('BigQuery'), ('Redshift'),

-- 기타
('GraphQL'), ('REST API'), ('Microservices'), ('Serverless'), ('Blockchain'),
('Web3'), ('Solidity'), ('Ethereum'), ('Bitcoin'), ('NFT'), ('DeFi'),
('WebAssembly'), ('WebRTC'), ('PWA'), ('AMP'), ('SEO'), ('Accessibility'),
('Security'), ('OAuth'), ('JWT'), ('OIDC'), ('SAML'), ('LDAP'), ('Active Directory'),
('CI/CD'), ('Git'), ('SVN'), ('Mercurial'), ('Bitbucket'), ('GitLab'), ('Azure DevOps'),
('Linux'), ('Ubuntu'), ('CentOS'), ('Red Hat'), ('Debian'), ('Alpine'), ('Windows Server'),
('Nginx'), ('Apache'), ('HAProxy'), ('Traefik'), ('Envoy'), ('Istio'), ('Linkerd');

-- 결제 상품 초기 데이터
INSERT INTO payment_products (name, amount, coin, active) VALUES 
('100코인', 10000, 100, true),           -- 1만원 = 100코인
('300코인', 25000, 300, true),           -- 2.5만원 = 300코인 (할인)
('500코인', 40000, 500, true),           -- 4만원 = 500코인 (할인)
('1000코인', 75000, 1000, true),         -- 7.5만원 = 1000코인 (할인)
('2000코인', 140000, 2000, true);        -- 14만원 = 2000코인 (할인)
