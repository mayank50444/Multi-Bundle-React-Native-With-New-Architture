const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');

const { readCommonBundle, getStartModuleId, relativePath, appendBusinessBundle  } = require('./bundle');

const commonBundle = readCommonBundle();

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('@react-native/metro-config').MetroConfig}
 */
const config = {
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  serializer: {
    createModuleIdFactory: () => {
      const bizModuleIdMap = new Map();
      let nextId = getStartModuleId();

      return (path) => {
        const modulePath = relativePath(path);

        let moduleId = commonBundle.get(modulePath) ?? bizModuleIdMap.get(modulePath);
        if (typeof moduleId !== 'number') {
          nextId += 1;
          moduleId = nextId;
          bizModuleIdMap.set(modulePath, moduleId);
          appendBusinessBundle(modulePath, moduleId);
        }

        return moduleId;
      };
    },
    processModuleFilter(module) {
      // console.log(module.path)
      //过滤掉path为__prelude__的一些模块（基础包内已有）
      if (module['path'].includes('__prelude__')) {
        return false;
      }
      if (module.path.includes('polyfill')) {
        return false;
      }

      const modulePath = relativePath(module.path);

      return !commonBundle.has(modulePath);
    },
    getPolyfills: () => [],
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
