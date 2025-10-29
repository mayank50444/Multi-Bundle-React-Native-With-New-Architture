 const fs = require('fs')
const path = require('path')
 const COMMON_BUNDLE_FINE_NAME = 'index.common.js.bundle.txt';

 function relativePath(filepath) {
  return path.relative(__dirname, filepath);
}

 function cleanCommonBundle() {
  if (fs.existsSync(COMMON_BUNDLE_FINE_NAME)) {
    fs.unlinkSync(COMMON_BUNDLE_FINE_NAME);
  }
}

 function appendCommonBundle(path, moduleId) {
  fs.appendFileSync(COMMON_BUNDLE_FINE_NAME, `${path}:${moduleId}\n`);
}


function appendBusinessBundle(filepath, moduleId) {
  const index = process.argv.indexOf('--entry-file')
  const entryFile = process.argv[index + 1];
  const bundleFilepath = path.parse(entryFile).name + ".bundle.txt";
  fs.appendFileSync(bundleFilepath, `${filepath}:${moduleId}\n`);
}


function readCommonBundle() {
  if (fs.existsSync(COMMON_BUNDLE_FINE_NAME)) {
    const content = fs.readFileSync(COMMON_BUNDLE_FINE_NAME, 'utf8');
    return new Map(content.split('\n')
    .filter(Boolean)
    .map(line => {
      const [path, moduleId] = line.split(':');
      return [path, parseInt(moduleId, 10)];
    }));
  }
  return new Map();
}

/**
 *
 *
 * @return {number} 
 */
function getStartModuleId() {
  const index = process.argv.indexOf('--entry-file')
  const entryFile = process.argv[index + 1];

  const moduleIdMap = {
    'index.js': 1_000_000,
    'index.common.js': 2_000_000_000,
    'index.biz.js': 3_000_000_000,
  };

  const startId = moduleIdMap[entryFile] ?? 1_000_000;
  return startId;
}

module.exports = {
  relativePath,
  cleanCommonBundle,
  appendCommonBundle,
  appendBusinessBundle,
  readCommonBundle,
  getStartModuleId,
};
