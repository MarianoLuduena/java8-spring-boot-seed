function fn() {
    var env = karate.env || 'local'; // get java system property 'karate.env'
    karate.log('karate.env system property was:', karate.env, '| using env:', env);

    var config = { // base config JSON
        baseUrl: 'http://localhost:8080',
        authorizationUrl: 'https://mercury-bff-inte.apps.ocp-np.sis.ad.bia.itau/bff/v1/login/sign-in',
        username: 'intiman1',
        password: 'Sarasa08'
    };

    // don't waste time waiting for a connection or if servers don't respond within 5 seconds
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);
    karate.configure('ssl', true);
    return config;
}
